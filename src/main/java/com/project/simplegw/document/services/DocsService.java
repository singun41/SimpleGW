package com.project.simplegw.document.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.project.simplegw.common.services.AlarmService;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.common.vos.ResultStatus;
import com.project.simplegw.common.vos.SseData;
import com.project.simplegw.document.dtos.CommentDTO;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsOptionsDTO;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.entities.Comment;
import com.project.simplegw.document.entities.Content;
import com.project.simplegw.document.entities.DocsOptions;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.document.repositories.CommentRepository;
import com.project.simplegw.document.repositories.ContentRepository;
import com.project.simplegw.document.repositories.DocsOptionsRepository;
import com.project.simplegw.document.repositories.DocsRepository;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.config.CacheConfig;
import com.project.simplegw.system.services.SseService;
import com.project.simplegw.system.vos.CacheNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class DocsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DocsRepository docsRepo;
    private final ContentRepository contentRepo;
    private final DocsConverter docsConverter;
    private final MemberService memberService;
    private final CommentConverter commentConverter;
    private final CommentRepository commentRepo;
    private final SseService sseService;
    private final DocsOptionsRepository docsOptionsRepo;
    private final AlarmService alarmService;

    private Map<Long, DocsDTO> top7NoticeMap = new ConcurrentHashMap<>();
    private Map<Long, DocsDTO> top5FreeboardMap = new ConcurrentHashMap<>();

    @Autowired
    public DocsService(
        DocsRepository docsRepo, ContentRepository contentRepo, DocsConverter docsConverter, MemberService memberService,
        CommentConverter commentConverter, CommentRepository commentRepo, SseService sseService, DocsOptionsRepository docsOptionsRepo,
        AlarmService alarmService
    ) {
        this.docsRepo = docsRepo;
        this.contentRepo = contentRepo;
        this.docsConverter = docsConverter;
        this.memberService = memberService;
        this.commentConverter = commentConverter;
        this.commentRepo = commentRepo;
        this.sseService = sseService;
        this.docsOptionsRepo = docsOptionsRepo;
        this.alarmService = alarmService;

        setNoticeFixedList();
        setFreeboardFixedList();
    }



    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //
    private DocsDTO docsToDto(Document entity) {
        DocsDTO docsDTO = docsConverter.getDto(entity);
        return docsDTO.setContent(contentRepo.getById(entity.getId()).getContent());
    }
    // private DocsDTO docsToDtoExcludeContent(Document entity) {
    //     return docsConverter.getDto(entity);
    // }
    private Document dtoToDocs(DocsDTO dto) {
        return docsConverter.getDocs(dto);
    }
    private List<DocsDTO> docsToDtos(List<Document> entities) {
        if(entities == null) return null;
        // List를 리턴할 때에는 내용은 불필요하므로 Document만 converting 후 리턴한다.
        return entities.stream().map(docsConverter::getDto).collect(Collectors.toList());
    }
    // content
    private Content dtoToContent(DocsDTO dto) {
        return docsConverter.getContent(dto);
    }
    // comment
    private Comment dtoToComment(CommentDTO dto) {
        return commentConverter.getEntity(dto);
    }
    private List<CommentDTO> commentsToDtos(List<Comment> entities) {
        if(entities == null) return null;
        return entities.stream().map(commentConverter::getDto).collect(Collectors.toList());
    }
    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    @Cacheable(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.BOARD, key = "#docsId", condition = "#type.name() == 'BOARD' and #docsId != null")
    public DocsDTO getDocsDto(Long docsId, DocumentType type, DocumentKind kind) { // 문서 번호로 검색: 문서 디테일 볼 때
        logger.info("docs id: {}, type: {}, kind: {}", docsId.toString(), type.name(), kind.name());
        Document docs = docsRepo.getByIdAndTypeAndKind(docsId, type, kind);
        if(docs == null)
            return null;

        return docsToDto(docs).setMemberId(docs.getMember().getId());   // convert 메서드에서 content를 바인딩해서 리턴.
    }

    // // 문서 수정 전용: ckeditor가 초기화되는 시간이 필요하기에 content를 제외한 내용만 전달, ckeditor에 바인딩할 내용은 별도로 호출한다.
    // public DocsDTO getDocsDtoExcludeContent(Long docsId, DocumentType type, DocumentKind kind) {
    //     return docsToDtoExcludeContent(docsRepo.getByIdAndTypeAndKind(docsId, type.name(), kind.name()));
    // }

    public Content getContent(Long docsId, DocumentType type, DocumentKind kind) {   // 결재문서의 Service 클래스들에서 사용
        Optional<Content> result = contentRepo.findByDocsIdAndTypeAndKind(docsId, type.name(), kind.name());
        return result.orElseGet(Content::new);
    }

    // 문서의 내용만 리턴
    public String getContentString(Long docsId, DocumentType type, DocumentKind kind) {
        return getContent(docsId, type, kind).getContent();
    }

    public List<DocsDTO> getDocsDtoList(DocsSearchDTO searchConditions) { // 문서 리스트 검색: 컨텐트 불필요.
        List<Document> docsList = docsRepo.findByTypeAndKindAndCreatedDateBetweenAndRegisteredOrderByIdDesc(
            searchConditions.getType(), searchConditions.getKind(), searchConditions.getDateStart(), searchConditions.getDateEnd(), searchConditions.isRegistered()
        );
        return docsToDtos(docsList);
    }
    public List<DocsDTO> getDocsDtoList(DocsSearchDTO searchConditions, Long memberId) { // 본인이 작성한 문서 리스트 검색: 컨텐트 불필요.
        List<Document> docsList = docsRepo.findByMemberIdAndTypeAndKindAndCreatedDateBetweenAndRegisteredOrderByIdDesc(
            memberId, searchConditions.getType(), searchConditions.getKind(), searchConditions.getDateStart(), searchConditions.getDateEnd(), searchConditions.isRegistered()
        );
        return docsToDtos(docsList);
    }
    public List<DocsDTO> getTemporaryList(Long memberId) {   // 유저별 임시저장 문서들: 컨텐트 불필요.
        List<DocsDTO> dtos = docsToDtos(docsRepo.findAllByMemberIdAndRegisteredOrderByIdDesc(memberId, false));
        dtos.forEach(dto -> dto.setKindTitle(DocumentKind.valueOf(dto.getKind()).getTitle()));   // 문서 유형별 타이틀 바인딩 후 리턴
        return dtos;
    }
    public List<DocsDTO> findByDate(DocsSearchDTO searchConditions) {
        List<Document> docsList = docsRepo.findByCreatedDateBetweenOrderByIdDesc(searchConditions.getDateStart(), searchConditions.getDateEnd());
        return docsToDtos(docsList);
    }
    public Document findById(Long docsId) { // 결재문서의 Service 클래스들에서 사용
        Optional<Document> result = docsRepo.findById(docsId);
        return result.orElseGet(Document::new);
    }
    
    public List<CommentDTO> findCommentsByDocsId(Long docsId) {
        return commentsToDtos(commentRepo.findAllByDocsIdOrderById(docsId));
    }

    public long getTemporarySavedCount(Long writerId) {
        return docsRepo.countByMemberIdAndRegistered(writerId, false);
    }

    // 회의록 검색 전용
    public List<DocsDTO> getMeetingMinutes(Long memberId, LocalDate dateStart, LocalDate dateEnd) {
        return docsToDtos(docsRepo.findAllMeetingMinutes(memberId, dateStart, dateEnd));
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- fixed list ----- ----- ----- ----- ----- //
    public List<DocsDTO> getFixedNoticeList() {
        return top7NoticeMap.values().stream().sorted(Comparator.comparing(DocsDTO::getId).reversed()).collect(Collectors.toList());
    }
    public List<DocsDTO> getFixedFreeboardList() {
        return top5FreeboardMap.values().stream().sorted(Comparator.comparing(DocsDTO::getId).reversed()).collect(Collectors.toList());
    }

    private List<Document> listFiltering(List<Document> list) {
        LinkedList<Document> newList = new LinkedList<>(
            list.stream().filter(docs -> {
                DocsOptions option = docsOptionsRepo.getByDocsId(docs.getId());
                if(option != null) {
                    return !option.getDueDate().isBefore(LocalDate.now());
                } else {
                    return true;
                }
            }).collect(Collectors.toList())
        );
        
        while(newList.size() > 7) {
            newList.removeLast();
        }
        return newList;
    }

    public void setNoticeFixedList() {   // 스케줄러에 등록하기 위해서 private --> public 으로 전환.
        // 메인 화면에 고정으로 보일 공지사항, 자유게시판 게시물 리스트
        top7NoticeMap.clear();

        List<Document> top7NoticeList = listFiltering(docsRepo.findTop20ByTypeAndKindAndRegisteredOrderByIdDesc(DocumentType.BOARD, DocumentKind.NOTICE, true));
        if(top7NoticeList.size() > 0) {
            List<DocsDTO> top7NoticeDtoList = docsToDtos(top7NoticeList);
            top7NoticeDtoList.forEach(notice -> top7NoticeMap.put(notice.getId(), notice));
            logger.info("공지사항 최신 게시글 7건을 로드하였습니다.");
        }
    }
    private void setFreeboardFixedList() {
        // 공지사항은 top 7, 자유게시판은 top 5
        top5FreeboardMap.clear();

        List<Document> top5FreeboardList = docsRepo.findTop5ByTypeAndKindAndRegisteredOrderByIdDesc(DocumentType.BOARD, DocumentKind.FREEBOARD, true);
        if(top5FreeboardList.size() > 0) {
            List<DocsDTO> top5FreeboardDtoList = docsToDtos(top5FreeboardList);
            top5FreeboardDtoList.forEach(freeboard -> top5FreeboardMap.put(freeboard.getId(), freeboard));
            
            logger.info("자유게시판 최신 게시글 5건을 로드하였습니다.");
        }
    }

    private void updateFixedBoardList(DocsDTO docsDTO, DocumentType type, DocumentKind kind) {
        if(docsDTO.isRegistered()) {
            if(type.equals(DocumentType.BOARD)) {
                if(kind.equals(DocumentKind.NOTICE)) {
                    setNoticeFixedList();   // DocsOptions 가 추가되어 map에 put하지 않고 리로드한다.
                    logger.info("공지사항 map 인스턴스 필드가 업데이트 되었습니다.");
                    // top7NoticeMap.put(docsDTO.getId(), docsDTO);
                    
                    // if(top7NoticeMap.size() > 7) {
                    //     logger.info("메모리에 있는 공지사항 게시글 중 오래된 1건을 제거합니다.");
                    //     Long firstId = top7NoticeMap.keySet().stream().sorted().findFirst().get();
                    //     top7NoticeMap.remove(firstId);
                    // }
                }
    
                if(kind.equals(DocumentKind.FREEBOARD)) {
                    top5FreeboardMap.put(docsDTO.getId(), docsDTO);
    
                    if(top5FreeboardMap.size() > 5) {
                        logger.info("메모리에 있는 자유게시판 게시글 중 오래된 1건을 제거합니다.");
                        Long firstId = top5FreeboardMap.keySet().stream().sorted().findFirst().get();
                        top5FreeboardMap.remove(firstId);
                    }
                    logger.info("자유게시판 map 인스턴스 필드가 업데이트 되었습니다.");
                }
            }
        }
    }

    private void deleteFixedBoardList(DocumentType type, DocumentKind kind) {
        // 지우고 빈 자리 만큼 다시 채워줘야 하는데, 데이터를 다시 가져오는게 코드상 깔끔하고 성능이슈도 별로 없기 때문에 변경.
        // remove --> setNoticeFixedList(), setFreeboardFixedList() 로 변경.
        
        if(type.equals(DocumentType.BOARD)) {
            if(kind.equals(DocumentKind.NOTICE)) {
                logger.info("공지사항 게시글을 삭제하여 리스트를 리로드합니다.");
                setNoticeFixedList();
            }
            if(kind.equals(DocumentKind.FREEBOARD)) {
                logger.info("자유게시판 게시글을 삭제하여 리스트를 리로드합니다.");
                setFreeboardFixedList();
            }
        }
    }
    // ----- ----- ----- ----- ----- fixed list ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- data handling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- title & content check ----- ----- ----- ----- ----- //
    private boolean isEmptyTitle(String title) {
        return title == null || title.isBlank();
    }
    private boolean isEmptyContent(String content) {
        return content == null || content.strip().isBlank();
    }
    // ----- ----- ----- ----- ----- title & content check ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- authority check ----- ----- ----- ----- ----- //
    private boolean getAuthority(Long memberId, DocumentType type) {
        MemberRole role = memberService.getMember(memberId).getRole();
        
        switch(type) {
            case BOARD:
                if(MemberRole.ADMIN.equals(role) || MemberRole.MANAGER.equals(role)) return true;
                else return false;
            case APPROVAL:
                if(MemberRole.ADMIN.equals(role)) return true;
                else return false;
            default:
                return false;
        }
    }
    // ----- ----- ----- ----- ----- authority check ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- insert ----- ----- ----- ----- ----- //
    private Document insertDocs(DocsDTO docsDTO, DocumentType type, DocumentKind kind) {
        docsDTO.setType(type.name()).setKind(kind.name());
        Document docs = dtoToDocs(docsDTO);
        docs.insertMemberDetails(memberService.searchMemberDetailsById(docsDTO.getMemberId()));

        Document savedDocs = docsRepo.save(docs);

        // 결재문서 저장시 content를 사용하지 않는 경우 공백체크를 건너뛰기 위해 null 문자열로 임시 입력. 저장시에는 다시 null로 처리.
        docsDTO.setContent(docsDTO.getContent().equals("null") ? null : docsDTO.getContent());
        contentRepo.save(dtoToContent(docsDTO).insertDocs(savedDocs));

        return savedDocs;
    }

    @CacheEvict(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.BOARD, key = "#docsDTO.id", condition = "#type.name() == 'BOARD' and #docsDTO.id != null")
    public RequestResult insertAndReturnResult(DocsDTO docsDTO, DocumentType type, DocumentKind kind) {
        docsDTO.setTitle(docsDTO.getTitle().strip());
        
        if(isEmptyTitle(docsDTO.getTitle()))
            return RequestResult.getDefaultFail("제목을 작성하세요.");
        if(isEmptyContent(docsDTO.getContent()))
            return RequestResult.getDefaultFail("내용을 작성하세요.");

        if(docsDTO.getTitle().length() > Constants.COLUMN_LENGTH_DOCU_TITLE) {
            return RequestResult.getDefaultFail("제목은 " + Constants.COLUMN_LENGTH_DOCU_TITLE + "자 이하로 작성하세요.");
        }

        boolean insertable = false;

        switch(type) {
            case BOARD:
                switch(kind) {
                    case NOTICE:
                        insertable = getAuthority(docsDTO.getMemberId(), type);
                        break;

                    default:   // 공지사항 외 나머지는 모두 허용.
                        insertable = true;
                        break;
                }
                break;

            case APPROVAL:
                insertable = true;
                break;
            
            default:
                return RequestResult.getDefaultError("처리할 수 없는 요청입니다.");
        }

        if(insertable) {
            if(docsDTO.getId() != null) {   // 임시저장 한 뒤 임시저장을 또 하거나, 등록으로 넘길 때
                return updateAndReturnResult(docsDTO, type, kind);
            }
            
            Document savedDocs = insertDocs(docsDTO, type, kind);
    
            if(type.equals(DocumentType.BOARD)) {   // 등록한 문서가 공지사항이나 자유게시판에 속하는 Board type이면 저장 완료된 문서를 고정 리스트에 등록.
                if(kind.equals(DocumentKind.NOTICE) || kind.equals(DocumentKind.FREEBOARD)) {
                    if(savedDocs.isRegistered()) {   // 임시저장 말고 최종 등록한 문서만 추가.
                        updateFixedBoardList(docsToDto(savedDocs), type, kind);
                        sseService.sendToClients(SseData.valueOf(kind.name()));   // 문서 종류에 맞는 SsdData type을 전달.
                    }
                }
            }

            // content와 attachments 저장을 위해 docs id 리턴
            return RequestResult.builder().status(ResultStatus.SUCCESS).message(null).returnObj(savedDocs.getId()).build();
        } else {
            return RequestResult.getDefaultFail(Constants.RESULT_MESSAGE_NOT_AUTHORIZED);
        }
    }
    // ----- ----- ----- ----- ----- insert ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- update ----- ----- ----- ----- ----- //
    private Document updateDocs(Document updateTarget, DocsDTO docsDTO, DocumentType type, DocumentKind kind) {
        // 제목, 수정시각 적용 후 save
        // 이미 등록된 문서를 임시 저장할 수 없게 만들기 위해서 'or' 연산
        Document savedDocs = docsRepo.save(updateTarget.updateTitle(docsDTO.getTitle()).updateRegistered(docsDTO.isRegistered() || updateTarget.isRegistered()));
        
        // 기존 문서 내용 로드 후 업데이트
        Content content = getContent(updateTarget.getId(), type, kind);
        contentRepo.save(content.updateContent(docsDTO.getContent().equals("null") ? null : docsDTO.getContent()));

        return savedDocs;
    }

    @CacheEvict(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.BOARD, key = "#docsDTO.id", condition = "#type.name() == 'BOARD' and #docsDTO.id != null")
    public RequestResult updateAndReturnResult(DocsDTO docsDTO, DocumentType type, DocumentKind kind) {
        docsDTO.setTitle(docsDTO.getTitle().strip());
        
        if(isEmptyTitle(docsDTO.getTitle()))
            return RequestResult.getDefaultFail("제목을 작성하세요.");
        if(isEmptyContent(docsDTO.getContent()))
            return RequestResult.getDefaultFail("내용을 작성하세요.");

        if(docsDTO.getTitle().length() > Constants.COLUMN_LENGTH_DOCU_TITLE) {
            return RequestResult.getDefaultFail("제목은 " + Constants.COLUMN_LENGTH_DOCU_TITLE + "자 이하로 작성하세요.");
        }

        // 기존 문서 불러오기
        Document updateTargetDocs = findById(docsDTO.getId());
        if(updateTargetDocs.getId() == null) {
            return RequestResult.getDefaultFail("문서가 이미 삭제되었습니다.");
        }

        boolean updatable = false;
        
        switch(type) {
            case BOARD:
                switch(kind) {
                    case NOTICE:
                        updatable = getAuthority(docsDTO.getMemberId(), type);
                        break;

                    default:
                        // update: writer only.
                        updatable = docsDTO.getMemberId().equals(updateTargetDocs.getMember().getId());
                        break;
                }
                break;

            case APPROVAL:
                updatable = docsDTO.getMemberId().equals(updateTargetDocs.getMember().getId());
                break;

            default:
                return RequestResult.getDefaultError("처리할 수 없는 요청입니다.");
        }
        
        if(updatable) {
            Document savedDocs = updateDocs(updateTargetDocs, docsDTO, type, kind);
    
            if(type.equals(DocumentType.BOARD)) {   // 수정한 문서가 공지사항이나 자유게시판에 속하는 Board type이면 수정된 문서를 고정 리스트에서 찾아 업데이트.
                if(kind.equals(DocumentKind.NOTICE) || kind.equals(DocumentKind.FREEBOARD)) {
                    if(savedDocs.isRegistered()) {   // 임시저장 말고 최종 등록한 문서만 추가.
                        updateFixedBoardList(docsToDto(savedDocs), type, kind);
                        sseService.sendToClients(SseData.valueOf(kind.name()));   // 문서 종류에 맞는 SsdData type을 전달.
                    }
                }
            }

            // attachments 저장을 위해 updateTargetDocs id 리턴
            return RequestResult.builder().status(ResultStatus.SUCCESS).message(Constants.RESULT_MESSAGE_OK).returnObj(updateTargetDocs.getId()).build();
        } else {
            return RequestResult.getDefaultFail(Constants.RESULT_MESSAGE_NOT_AUTHORIZED);
        }
    }
    // ----- ----- ----- ----- ----- update ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- delete ----- ----- ----- ----- ----- //
    @CacheEvict(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.BOARD, key = "#docsId", condition = "#type.name() == 'BOARD' and #docsId != null")
    public RequestResult deleteDocs(Long docsId, Long memberId, DocumentType type, DocumentKind kind) {
        boolean deletable = false;

        // 기존 문서 불러오기
        Document deleteTargetDocu = findById(docsId);
        if(deleteTargetDocu.getId() == null) {
            return RequestResult.getDefaultFail("문서가 이미 삭제되었습니다.");
        }

        switch(type) {
            case BOARD:
                switch(kind) {
                    case NOTICE:
                        deletable = getAuthority(memberId, type);
                        break;

                    default:
                        deletable = (getAuthority(memberId, type) || memberId.equals(deleteTargetDocu.getMember().getId()));
                        break;
                }
                break;


            case APPROVAL:
                deletable = memberId.equals(deleteTargetDocu.getMember().getId());
                break;

            default:
                return RequestResult.getDefaultError("처리할 수 없는 요청입니다.");
        }

        if(deletable) {
            docsRepo.delete(deleteTargetDocu);

            if(type.equals(DocumentType.BOARD)) {
                if(kind.equals(DocumentKind.NOTICE) || kind.equals(DocumentKind.FREEBOARD)) {
                    if(deleteTargetDocu.isRegistered()) {
                        deleteFixedBoardList(type, kind);
                        sseService.sendToClients(SseData.valueOf(kind.name()));   // 문서 종류에 맞는 SsdData type을 전달.
                    }
                }
            }

            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);
        } else {
            return RequestResult.getDefaultFail(Constants.RESULT_MESSAGE_NOT_AUTHORIZED);
        }
    }
    // ----- ----- ----- ----- ----- delete ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- comment insert & delete (update는 없음) ----- ----- ----- ----- ----- //
    public RequestResult insertComment(CommentDTO commentDTO) {
        commentDTO.setComment(commentDTO.getComment().strip());

        if(commentDTO.getComment() == null || commentDTO.getComment().isBlank()) {
            return RequestResult.getDefaultFail("빈 문자열은 등록할 수 없습니다.");
        }

        if(commentDTO.getComment().length() > Constants.COLUMN_LENGTH_COMMENT) {
            return RequestResult.getDefaultFail(Constants.COLUMN_LENGTH_COMMENT + "자 이하로 작성하세요.");
        }

        Document docs = findById(commentDTO.getDocsId());
        if(docs.getId() == null) {
            return RequestResult.getDefaultFail("문서가 삭제되어 등록할 수 없습니다.");
        }

        MemberDetails memberDetails = memberService.searchMemberDetailsById(commentDTO.getWriterId());
        commentDTO.setWriterTeam(memberDetails.getTeam()).setWriterJobTitle(memberDetails.getJobTitle()).setWriterName(memberDetails.getName());

        // 작성자에게 알림 보내기
        Long docsWriter = docs.getMember().getId();
        if(commentDTO.getWriterId() != docsWriter) {   // 본인이 본인 게시글에 작성할 때에는 알림을 발송하지 않는다.
            alarmService.insertNewAlarm(
                docsWriter,
                new StringBuilder("번호: ").append(docs.getId().toString()).append(", 제목: \"").append(docs.getTitle()).append("\" 문서에 새로운 댓글이 달렸습니다.").toString()
            );
        }

        Comment comment = dtoToComment(commentDTO);
        commentRepo.save(comment.insertDocs(docs));
        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_INSERTED);
    }

    public RequestResult deleteComment(CommentDTO commentDTO, MemberRole role) {
        Optional<Comment> result = commentRepo.findById(commentDTO.getId());
        Comment comment;
        
        if(result.isPresent()) {
            comment = result.get();

            if(role.equals(MemberRole.ADMIN) || role.equals(MemberRole.MANAGER) || comment.getWriterId().equals(commentDTO.getWriterId())) {
                commentRepo.delete(dtoToComment(commentDTO));
                return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);

            } else {
                return RequestResult.getDefaultFail("다른 유저가 등록한 코멘트는 삭제할 수 없습니다.");
            }

        } else {
            return RequestResult.getDefaultFail("댓글이 이미 삭제되었습니다.");
        }
    }
    // ----- ----- ----- ----- ----- comment insert & delete (update는 없음) ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- data handling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- //
    public void saveDocsOptions(DocsOptionsDTO dto) {
        if(dto.isUse()) {
            if(dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())) {
                dto.setDueDate(null);   // 설정한 날짜가 오늘 이전이면 null 처리.
            }

            // 모든 옵션들이 null이면 종료, 다른 옵션이 추가되면 여기 조건에 추가.
            if(dto.getDueDate() == null /* && dto.getOtherOption() */) {
                return;
            }

            Document docs = findById(dto.getDocsId());

            // 공지사항만 적용, 임시저장된 문서는 적용하지 않음.
            if(docs.getKind().equals(DocumentKind.NOTICE) && docs.isRegistered()) {
                DocsOptions entity = docsOptionsRepo.getByDocsId(dto.getDocsId());
                if(entity == null) {
                    entity = docsConverter.getOptions(dto).updateOptions(dto);
                } else {
                    entity.updateOptions(dto);
                }
                docsOptionsRepo.save(entity.insertDocs(docs));
            }
        }
    }
    public DocsOptionsDTO getDocsOptions(Long docsId) {
        DocsOptionsDTO dto = docsConverter.getDto(docsOptionsRepo.getByDocsId(docsId));
        if(dto == null)
            return new DocsOptionsDTO();
        else
            return dto.setUse(true);
    }
    // ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- //
}