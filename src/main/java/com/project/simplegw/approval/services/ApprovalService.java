package com.project.simplegw.approval.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.ApprovalDocsDTO;
import com.project.simplegw.approval.dtos.ApprovalLineDTO;
import com.project.simplegw.approval.dtos.ApproverDTO;
import com.project.simplegw.approval.dtos.DayoffDTO;
import com.project.simplegw.approval.dtos.DayoffDocsDTO;
import com.project.simplegw.approval.dtos.DocsApprovalDTO;
import com.project.simplegw.approval.dtos.NamecardDTO;
import com.project.simplegw.approval.dtos.NamecardDocsDTO;
import com.project.simplegw.approval.dtos.OvertimeDTO;
import com.project.simplegw.approval.dtos.OvertimeDocsDTO;
import com.project.simplegw.approval.dtos.PurchaseDTO;
import com.project.simplegw.approval.dtos.PurchaseDocsDTO;
import com.project.simplegw.approval.dtos.ReceivedDocsDTO;
import com.project.simplegw.approval.dtos.ReferrerDTO;
import com.project.simplegw.approval.dtos.TemplateLineDetailsDTO;
import com.project.simplegw.approval.dtos.TemplateLineMasterDTO;
import com.project.simplegw.approval.entities.ApprovalDocStatus;
import com.project.simplegw.approval.entities.Approver;
import com.project.simplegw.approval.entities.Referrer;
import com.project.simplegw.approval.entities.TemplateLineDetails;
import com.project.simplegw.approval.entities.TemplateLineMaster;
import com.project.simplegw.approval.repositories.ApprovalDocStatusRepository;
import com.project.simplegw.approval.repositories.ApproverRepository;
import com.project.simplegw.approval.repositories.ReferrerRepository;
import com.project.simplegw.approval.repositories.TemplateLineDetailsRepository;
import com.project.simplegw.approval.repositories.TemplateLineMasterRepository;
import com.project.simplegw.approval.vos.ApproverRole;
import com.project.simplegw.approval.vos.ApproverStatus;
import com.project.simplegw.common.services.AlarmService;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.common.vos.ResultStatus;
import com.project.simplegw.common.vos.SseData;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.services.ScheduleService;
import com.project.simplegw.system.config.CacheConfig;
import com.project.simplegw.system.services.SseService;
import com.project.simplegw.system.vos.CacheNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ApprovalService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;
    private final ApprovalConverter approvalConverter;
    private final DocsService docsService;
    private final TemplateLineMasterRepository lineMasterRepo;
    private final TemplateLineDetailsRepository lineDetailsRepo;
    private final ApprovalDocStatusRepository approvalDocStatusRepo;
    private final ApproverRepository approverRepo;
    private final ReferrerRepository referrerRepo;
    private final SseService sseService;
    private final AlarmService alarmService;
    private final ScheduleService scheduleService;

    // SubListService 리스트
    private final DayoffService dayoffService;
    private final PurchaseService purchaseService;
    private final OvertimeService overtimeService;
    private final NamecardService namecardService;

    @Autowired
    public ApprovalService(MemberService memberService, ApprovalConverter approvalConverter, DocsService docsService,
        TemplateLineMasterRepository lineMasterRepo, TemplateLineDetailsRepository lineDetailsRepo,
        ApprovalDocStatusRepository approvalDocStatusRepo,
        ApproverRepository approverRepo, ReferrerRepository referrerRepo, SseService sseService,
        AlarmService alarmService, ScheduleService scheduleService,
        DayoffService dayoffService, PurchaseService purchaseService, OvertimeService overtimeService, NamecardService namecardService
    ) {
        this.memberService = memberService;
        this.approvalConverter = approvalConverter;
        this.docsService = docsService;
        this.lineMasterRepo = lineMasterRepo;
        this.lineDetailsRepo = lineDetailsRepo;
        this.approvalDocStatusRepo = approvalDocStatusRepo;
        this.approverRepo = approverRepo;
        this.referrerRepo = referrerRepo;
        this.sseService = sseService;
        this.alarmService = alarmService;
        this.scheduleService = scheduleService;

        this.dayoffService = dayoffService;
        this.purchaseService = purchaseService;
        this.overtimeService = overtimeService;
        this.namecardService = namecardService;
    }

    @Cacheable(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.APPROVAL_DOCS_KINDS)
    public List<DocumentKind> getApprovalKinds() {
        logger.info("결재문서 종류를 캐시에 로드하였습니다.");
        return Arrays.stream(DocumentKind.values()).filter(e ->
            !(e.equals(DocumentKind.NOTICE) || e.equals(DocumentKind.FREEBOARD) || e.equals(DocumentKind.MEETING) || e.equals(DocumentKind.ARCHIVE) || e.equals(DocumentKind.SUGGESTION))
        ).sorted(Comparator.comparing(DocumentKind::getTitle)).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.APPROVER_ROLES)
    public List<ApproverRole> getApproverRoles() {
        logger.info("결재자, 참조자 구분값을 캐시에 로드하였습니다.");
        return Arrays.stream(ApproverRole.values()).filter(e -> !e.equals(ApproverRole.SUBMITTER)).collect(Collectors.toList());
    }

    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //
    private List<TemplateLineMasterDTO> lineMastersToDtos(List<TemplateLineMaster> entities) {
        if(entities == null) return null;
        return entities.stream().map(approvalConverter::getDto).collect(Collectors.toList());
    }
    private List<ApproverDTO> approversToDtos(List<Approver> entities) {
        if(entities == null) return null;
        return entities.stream().map(entity -> approvalConverter.getDto(entity).setMemberId(entity.getApprover().getId())).collect(Collectors.toList());
    }
    private List<ReferrerDTO> referrersToDtos(List<Referrer> entities) {
        if(entities == null) return null;
        return entities.stream().map(entity -> approvalConverter.getDto(entity).setMemberId(entity.getReferrer().getId())).collect(Collectors.toList());
    }
    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Counting ----- ----- ----- ----- ----- //
    public long getReceivedApprovalDocsCount(ApproverRole role, Long receiverId) {
        switch(role) {
            case APPROVER:
                // 현재 내 순서에서 결재 대기중인 문서 카운트
                return approverRepo.countReceivedAprovalDocs(receiverId);

            case REFERRER:
                // 확인하지 않은 참조로 받은 결재문서 카운트
                return referrerRepo.countReceivedReferenceDocs(receiverId);

            default:
                return 0;
        }
    }

    // 진행중인 결재문서
    public long proceedingDocsCount(Long writerId) {
        return approvalDocStatusRepo.proceedingDocsCount(writerId);
    }
    // ----- ----- ----- ----- ----- Counting ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    private ApprovalDocStatus getDocStatus(Long docsId) {
        return approvalDocStatusRepo.getByDocsId(docsId);
    }

    public List<DocsApprovalDTO> getDocsApprovalDtoList(DocsSearchDTO searchConditions, Long memberId) {
        List<DocsDTO> docsList = docsService.getDocsDtoList(searchConditions, memberId);
        List<DocsApprovalDTO> docsApprovalList = new ArrayList<>();

        docsList.forEach(docs -> {
            ApprovalDocStatus docsStatus = getDocStatus(docs.getId());
            if(docsStatus != null) {
                docsApprovalList.add( approvalConverter.getDto(docsStatus).setId(docs.getId()).setTitle(docs.getTitle()).setCreatedDate(docs.getCreatedDate()) );
            }
        });
        
        return docsApprovalList;
    }

    public Map<String, Object> getSavedLine(Long docsId) {
        Map<String, Object> result = new HashMap<>();
        result.put("approvers", approversToDtos(approverRepo.findAllByDocsId(docsId)));
        result.put("referrers", referrersToDtos(referrerRepo.findAllByDocsId(docsId)));
        return result;
    }

    // 수신문서: 내 순서에 대기중인 결재문서 / 미확인 참조 문서
    public List<ReceivedDocsDTO> getReceivedDocsList(ApproverRole role, Long receiverId, DocumentKind kind) {
        List<ReceivedDocsDTO> docsList = new ArrayList<>();
        List<Object[]> result = null;
        
        switch(role) {
            case APPROVER:
                result = approverRepo.findAllReceivedApprovalDocs(receiverId, kind);
                break;

            case REFERRER:
                result = referrerRepo.findAllReceivedReferenceDocs(receiverId, kind);
                break;

            default:
                break;
        }

        if(result.size() > 0) {
            docsList = result.stream().map(obj -> {
                ReceivedDocsDTO docs = new ReceivedDocsDTO(obj);
                return docs.setKindTitle(DocumentKind.valueOf(docs.getKind()).getTitle());
            }).collect(Collectors.toList());
        }

        return docsList;
    }

    // 수신문서: 결재/참조 수신문서의 기간 조회
    public List<ReceivedDocsDTO> getReceivedDocsList(ApproverRole role, Long receiverId, DocumentKind kind, LocalDate dateStart, LocalDate dateEnd) {
        List<ReceivedDocsDTO> docsList = new ArrayList<>();
        List<Object[]> result = null;

        switch(role) {
            case APPROVER:
                result = approverRepo.findAllReceivedApprovalDocs(receiverId, kind, dateStart, dateEnd);
                break;

            case REFERRER:
                result = referrerRepo.findAllReceivedReferenceDocs(receiverId, kind, dateStart, dateEnd);
                break;

            default:
                break;
        }

        if(result.size() > 0) {
            docsList = result.stream().map(obj -> {
                ReceivedDocsDTO docs = new ReceivedDocsDTO(obj);
                return docs.setKindTitle(DocumentKind.valueOf(docs.getKind()).getTitle());
            }).collect(Collectors.toList());
        }

        return docsList;
    }

    // 진행중인 결재문서
    public List<DocsApprovalDTO> getProceedingDocs(Long writerId) {
        List<DocsApprovalDTO> docsList = new ArrayList<>();
        List<Object[]> result = approvalDocStatusRepo.findAllProceedingDocs(writerId);
        if(result.size() > 0) {
            docsList = result.stream().map(obj -> {
                DocsApprovalDTO docs = new DocsApprovalDTO(obj);
                return docs.setKindTitle(docs.getKind().getTitle());
            }).collect(Collectors.toList());
        }
        return docsList;
    }

    // 완결된 결재문서
    public List<DocsApprovalDTO> getFinishedList(Long writerId, LocalDate dateStart, LocalDate dateEnd) {
        List<DocsApprovalDTO> docsList = new ArrayList<>();
        List<Object[]> result = approvalDocStatusRepo.findAllFinishedDocs(writerId, dateStart, dateEnd);
        if(result.size() > 0) {
            docsList = result.stream().map(obj -> {
                DocsApprovalDTO docs = new DocsApprovalDTO(obj);
                return docs.setKindTitle(docs.getKind().getTitle());
            }).collect(Collectors.toList());
        }
        return docsList;
    }

    public boolean isCurrentApprover(Long docsId, Long approverId) {
        ApprovalDocStatus status = getDocStatus(docsId);
        if(status.isFinished())
            return false;
        else
            return approverId.equals(status.getApproverId());
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Line Template handling ----- ----- ----- ----- ----- //
    public RequestResult approvalLineTemplateSave(ApprovalLineDTO approvalLineDTO, Long ownerId) {
        try {
            String title = approvalLineDTO.getTitle().strip();
            if(title == null || title.isBlank()) {
                return RequestResult.getDefaultFail("결재라인 제목을 입력하세요.");
            }

            List<Long> approverList = new ArrayList<>(Arrays.asList(approvalLineDTO.getArrApprover()));
            List<Long> referrerList = new ArrayList<>(Arrays.asList(approvalLineDTO.getArrReferrer()));

            if(approverList.get(0).equals(ownerId)) {
                approverList.remove(0);   // 첫번째로 자기 자신을 추가했다면 삭제. 시스템에서 알아서 등록한다. 아래 코드에서 등록함.
            }
            if(approverList.stream().filter(id -> id.equals(ownerId)).findFirst().isPresent()) {
                return RequestResult.getDefaultFail("결재라인의 첫 순서가 아닌 곳에 본인이 들어갈 수 없습니다.");
            }
            if(referrerList.stream().filter(id -> id.equals(ownerId)).findFirst().isPresent()) {
                return RequestResult.getDefaultFail("참조에는 본인을 추가할 수 없습니다.");
            }
            if(approverList.size() != approverList.stream().distinct().count()) {
                return RequestResult.getDefaultFail("결재자 라인에 중복된 멤버가 있습니다.");
            }
            if(referrerList.size() != referrerList.stream().distinct().count()) {
                return RequestResult.getDefaultFail("참조자 라인에 중복된 멤버가 있습니다.");
            }

            List<TemplateLineMaster> savedList = lineMasterRepo.findAllByOwnerId(ownerId);
            if(approvalLineDTO.getMasterId() == null || approvalLineDTO.getMasterId().equals(Long.valueOf(0))) {   // 새로 등록할 때
                if(savedList.stream().filter(m -> m.getTitle().equals(title)).findFirst().isPresent()) {
                    return RequestResult.getDefaultFail("결재라인 제목이 중복되었습니다.");
                }
            }

            Optional<TemplateLineMaster> searchedMaster = savedList.stream().filter(m -> m.getId().equals(approvalLineDTO.getMasterId())).findFirst();
            TemplateLineMaster master;

            String resultMsg;
            if(searchedMaster.isPresent()) {   // 기존 건이 있으면 삭제하고 새로 등록(=업데이트)
                master = searchedMaster.get();   // master entity를 바인딩하기 위해 가져오기

                List<TemplateLineDetails> originDetailsList = lineDetailsRepo.findAllByMasterId(approvalLineDTO.getMasterId());
                lineDetailsRepo.deleteAll(originDetailsList);
                lineDetailsRepo.flush();   // hibernate 동작 순서에 의해 insert --> update --> delete 순으로 처리된다. 여기서 flush를 해주면 delete가 먼저 동작한다.
                
                resultMsg = Constants.RESULT_MESSAGE_UPDATED;

            } else {
                master = lineMasterRepo.save(TemplateLineMaster.builder().id(null).owner(memberService.searchMemberDetailsById(ownerId)).title(title).build());
                resultMsg = Constants.RESULT_MESSAGE_INSERTED;
            }

            List<TemplateLineDetails> detailsList = new ArrayList<>();
            // 결재라인 첫번째는 등록자
            TemplateLineDetails first = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.SUBMITTER).seq(0).member(memberService.searchMemberDetailsById(ownerId)).build();
            detailsList.add(first);
    
            // 결재
            approverList.forEach(approver -> {
                TemplateLineDetails details = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.APPROVER).seq(approverList.indexOf(approver)+1).member(memberService.searchMemberDetailsById(approver)).build();
                detailsList.add(details);
            });
    
            // 참조
            referrerList.forEach(referrer -> {
                TemplateLineDetails details = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.REFERRER).seq(0).member(memberService.searchMemberDetailsById(referrer)).build();
                detailsList.add(details);
            });
    
            lineDetailsRepo.saveAll(detailsList);
            return RequestResult.getDefaultSuccess(resultMsg);

        } catch(Exception e) {
            e.printStackTrace();
            logger.warn("{}{}결재라인 템플릿 저장 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
            return RequestResult.getDefaultError("결재라인 템플릿 저장 에러가 발생하였습니다.");
        }
    }

    public List<TemplateLineMasterDTO> getSavedTemplateLineMasterList(Long ownerId) {
        return lineMastersToDtos(lineMasterRepo.findAllByOwnerId(ownerId));
    }

    public TemplateLineDetailsDTO getSavedTemplateLineDetailsList(Long masterId) {
        List<TemplateLineDetails> detailsList = lineDetailsRepo.findAllByMasterId(masterId);

        List<ApproverDTO> approverList = new ArrayList<>();
        List<ReferrerDTO> referrerList = new ArrayList<>();

        detailsList.forEach(details -> {
            MemberDetails member = details.getMember();
            if(details.getRole().equals(ApproverRole.APPROVER)) {
                ApproverDTO approver = new ApproverDTO();
                approver.setMemberId(member.getId()).setSeq(details.getSeq()).setTeam(member.getTeam()).setJobTitle(member.getJobTitle()).setName(member.getName());
                approverList.add(approver);
            }
            if(details.getRole().equals(ApproverRole.REFERRER)) {
                ReferrerDTO referrer = new ReferrerDTO();
                referrer.setMemberId(member.getId()).setTeam(member.getTeam()).setJobTitle(member.getJobTitle()).setName(member.getName());
                referrerList.add(referrer);
            }
        });
        return new TemplateLineDetailsDTO().setApproverList(approverList).setReferrerList(referrerList);
    }

    public RequestResult deleteTemplateLine(Long ownerId, Long masterId) {
        TemplateLineMaster target = lineMasterRepo.getById(masterId);
        if(target == null) {
            return RequestResult.getDefaultFail("이미 삭제되었습니다.");

        } else {
            if(target.getOwner().getId().equals(ownerId)) {
                lineMasterRepo.delete(target);
                return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);
            
            } else {
                return RequestResult.getDefaultFail("결재라인 소유자가 아닙니다.");
            }
        }
    }
    // ----- ----- ----- ----- ----- Line Template handling ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Line save ----- ----- ----- ----- ----- //
    private void saveApprovalLine(Long docsId, Long[] arrApprover, Long[] arrReferrer, boolean isRegistered) {
        Document docs = docsService.findById(docsId);
        
        // 기존 라인을 삭제 후 저장.
        approverRepo.deleteByDocsId(docsId);
        referrerRepo.deleteByDocsId(docsId);
        approverRepo.flush();
        referrerRepo.flush();

        ArrayList<Long> approverList = new ArrayList<>( Arrays.stream(arrApprover).distinct().collect(Collectors.toList()) );   // 중복제거
        ArrayList<Long> referrerList = new ArrayList<>( Arrays.stream(arrReferrer).distinct().collect(Collectors.toList()) );   // 중복제거

        ArrayList<Approver> approvers = new ArrayList<>();
        ArrayList<Referrer> referrers = new ArrayList<>();
        
        MemberDetails writerDetails = docs.getMember();
        if(approverList.get(0).equals(writerDetails.getId())) {   // 첫번째 순서에 본인이 들어가 있으면 삭제. 이유: 시스템에서 항상 자동으로 등록하기 위해서.
            approverList.remove(0);
        }
        Approver writer = Approver.builder().docs(docs).seq(0).status(ApproverStatus.SUBMITTED).checkedDatetime(LocalDateTime.now()).build();
        approvers.add(writer.insertApprover(writerDetails));
        
        approverList.forEach(memberId -> {
            int approverSeq = approverList.indexOf(memberId) + 1;
            Approver approver = Approver.builder().docs(docs).seq(approverSeq).status(approverSeq == 1 ? ApproverStatus.PROCEED : ApproverStatus.WAITING).build();
            approvers.add(approver.insertApprover(memberService.searchMemberDetailsById(memberId)));
        });

        referrerList.forEach(memberId -> {
            Referrer referrer = Referrer.builder().docs(docs).build();
            referrers.add(referrer.insertReferrer(memberService.searchMemberDetailsById(memberId)));
        });

        approverRepo.saveAll(approvers);
        referrerRepo.saveAll(referrers);

        ApprovalDocStatus docsStatus = approvalDocStatusRepo.getByDocsId(docsId);
        if(docsStatus != null) {
            approvalDocStatusRepo.delete(docsStatus);
            approvalDocStatusRepo.flush();
        }
        // 결재문서의 상태정보를 저장한다.
        // approverCount는 작성자는 제외하고 카운트하기 위해서 -1 해준다.
        ApprovalDocStatus docStatus = ApprovalDocStatus.builder().docs(docs)
                                        .writerId(approvers.get(0).getApprover().getId()).approverCount(approvers.size() - 1)
                                        .status(ApproverStatus.PROCEED)
                                        .build();
        docStatus.updateNextApprover(approvers.get(1));
        approvalDocStatusRepo.save(docStatus);

        if(isRegistered) {   // 임시저장이 아닌 등록한 문서의 경우만 알람을 전달한다.
            // 다음 순번 결재자에게 sse로 알림.
            alarmToApprover(approvers.get(1));

            // 참조에 들어있는 모든 유저에게 sse로 알림.
            alarmToReferrers(referrers);
        }
    }
    // ----- ----- ----- ----- ----- Line save ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Sse Alarm  ----- ----- ----- ----- ----- //
    private void alarmToApprover(Approver approver) {
        sseService.sendToClientForApproval(approver.getApprover().getId(), SseData.APPROVER);
    }
    private void alarmToReferrers(List<Referrer> referrers) {
        referrers.stream().forEach(referrer -> sseService.sendToClientForApproval(referrer.getReferrer().getId(), SseData.REFERRER));
    }
    private void alarmToSubmitter(Long submitterId, ApproverStatus status, Long docsId, DocumentKind kind, String title) {
        sseService.sendToSubmitter(submitterId, SseData.valueOf(status.name()), docsId, kind, title);
        alarmService.insertNewAlarm(
            submitterId,
            new StringBuilder("번호: ").append(docsId.toString()).append(", 제목: \"").append(title).append("\" 결재가 ").append(status.getTitle()).append(" 되었습니다.").toString()
        );
    }
    // ----- ----- ----- ----- ----- Sse Alarm  ----- ----- ----- ----- ----- //

    

    // ----- ----- ----- ----- ----- approval common method ----- ----- ----- ----- ----- //
    // insert, update, delete는 DocsService의 메서드로 사용한다.
    // 따라서 그 전에 결재라인 등 체크해야 할 것들을 먼저 처리하고 DocsService로 넘긴다.
    private boolean isEmptyApproverLine(Long[] approvers) {
        return (approvers.length == 0);
    }

    private String setApprovalContent(DocumentKind kind, String content) {
        String returnString = null;
        // DocsService의 insertAndReturnResult를 사용한다.
        // 문서 종류에 따라 Content 엔티티를 사용하지 않는 경우 텍스트 강제 기입해서 DocsService의 isEmptyContent에서 걸리지 않도록 한다.
        switch(kind) {
            case DAYOFF:
            case PURCHASE:
            case OVERTIME:
            case NAMECARD:
                returnString = (content == null || content.strip().isBlank()) ? "null" : content.strip();
                break;
            default:
                returnString = content;
                break;
        }
        return returnString;
    }

    private DocsDTO getDocsFromApprovalDocsDTO(ApprovalDocsDTO approvalDocsDTO) {
        return approvalConverter.getDto(approvalDocsDTO);
    }

    public boolean isProceedDocs(Long docsId) {   // viewController 에서도 같이 사용하기 위해 public으로 선언
        if(docsId == null)
            return false;
        
        ApprovalDocStatus docsStatus = approvalDocStatusRepo.getByDocsId(docsId);
        if(docsStatus == null)
            return false;
        
        else
            if(docsStatus.isFinished())
                return true;
            
            else if(docsStatus.getApproverSeq() > 1)   // 1보다 크다 --> 직후 결재자가 결재를 완료했으므로 진행중인 문서임. 수정, 삭제 불가.
                return true;

            else if(docsStatus.getApproverSeq() == 1 && (docsStatus.getStatus().equals(ApproverStatus.CONFIRMED) || docsStatus.getStatus().equals(ApproverStatus.REJECTED)))   // 직후 결재자 상태인 경우 --> 결재라인이 직후 결재자 1명만 있는 경우도 포함하기 때문에 CONFIRMED or REJECTED를 체크.
                return true;
            
            else
                return false;
    }
    // ----- ----- ----- ----- ----- approval common method ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Insert ----- ----- ----- ----- ----- //
    public RequestResult insertAndReturnResult(ApprovalDocsDTO approvalDocsDTO, DocumentKind kind) {
        if(isEmptyApproverLine(approvalDocsDTO.getApprovers()))   //결재라인만 검사. 참조는 필수가 아니라서 체크하지 않음.
            return RequestResult.getDefaultFail("결재라인을 설정하세요.");

        if(isProceedDocs(approvalDocsDTO.getId()))
            return RequestResult.getDefaultFail("결재가 진행되어 수정할 수 없습니다.");

        approvalDocsDTO.setContent(setApprovalContent(kind, approvalDocsDTO.getContent()));
        DocsDTO docsDTO = getDocsFromApprovalDocsDTO(approvalDocsDTO);

        RequestResult result = docsService.insertAndReturnResult(docsDTO, DocumentType.APPROVAL, kind);
        if(result.getStatus().equals(ResultStatus.SUCCESS)) {
            saveApprovalLine((Long) result.getReturnObj(), approvalDocsDTO.getApprovers(), approvalDocsDTO.getReferrers(), docsDTO.isRegistered());
        }
        return result;
    }
    // ----- ----- ----- ----- ----- Insert ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Update ----- ----- ----- ----- ----- //
    public RequestResult updateAndReturnResult(ApprovalDocsDTO approvalDocsDTO, DocumentKind kind) {
        if(isEmptyApproverLine(approvalDocsDTO.getApprovers()))   //결재라인만 검사. 참조는 필수가 아니라서 체크하지 않음.
            return RequestResult.getDefaultFail("결재라인을 설정하세요.");

        if(isProceedDocs(approvalDocsDTO.getId()))
            return RequestResult.getDefaultFail("결재가 진행되어 수정할 수 없습니다.");

        approvalDocsDTO.setContent(setApprovalContent(kind, approvalDocsDTO.getContent()));
        DocsDTO docsDTO = getDocsFromApprovalDocsDTO(approvalDocsDTO);

        RequestResult result = docsService.updateAndReturnResult(docsDTO, DocumentType.APPROVAL, kind);
        if(result.getStatus().equals(ResultStatus.SUCCESS)) {
            saveApprovalLine((Long) result.getReturnObj(), approvalDocsDTO.getApprovers(), approvalDocsDTO.getReferrers(), docsDTO.isRegistered());
        }
        return result;
    }
    // ----- ----- ----- ----- ----- Update ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Delete ----- ----- ----- ----- ----- //
    public RequestResult deleteAndReturnResult(Long docsId, Long memberId, DocumentKind kind) {
        if(isProceedDocs(docsId))
            return RequestResult.getDefaultFail("결재가 진행되어 삭제할 수 없습니다.");
        
        return docsService.deleteDocs(docsId, memberId, DocumentType.APPROVAL, kind);
    }
    // ----- ----- ----- ----- ----- Delete ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Approver and Referrer ----- ----- ----- ----- ----- //
    @Async   // 결재문서의 참조자로 설정된 경우 페이지를 열 때 확인시간을 업데이트한다.
    public void updateReferrerChecked(Long docsId, Long referrerId) {
        List<Referrer> referrers = referrerRepo.findAllByDocsId(docsId);
        if(referrers == null) return;

        Optional<Referrer> result = referrers.stream().filter(elem -> elem.getReferrer().getId().equals(referrerId)).findFirst();
        if(result.isPresent()) {
            Referrer referrer = result.get();

            if(referrer.getCheckedDatetime() == null) {   // 데이터가 없는 경우 최초 1번만 업데이트한다.
                referrerRepo.save(referrer.updateCheckedDatetime());
            }
        }
    }

    @Async   // 결재문서 승인 또는 반려 처리.
    public void updateApproverStatus(Long docsId, Long approverId, ApproverStatus status) {
        List<Approver> approvers = approverRepo.findAllByDocsId(docsId);
        Optional<Approver> result = approvers.stream().filter(elem -> elem.getApprover().getId().equals(approverId)).findFirst();

        if(result.isPresent()) {   // 결재 라인에 포함된 인원인 경우만 진행해야 한다. 임의로 api를 호출할 수도 있는데 그러면 null이므로 진행되지 않는다.
            Approver currentApprover = result.get();
            approverRepo.save(currentApprover.updateStatus(status));

            ApprovalDocStatus docStatus = approvalDocStatusRepo.getByDocsId(docsId);
            int currentSeq = approvers.indexOf(currentApprover);

            if(status.equals(ApproverStatus.CONFIRMED)) {
                if(currentSeq < approvers.size() - 1) {

                    Approver nextApprover = approvers.get(currentSeq + 1);
                    approverRepo.save(nextApprover.updateStatusToProceed());

                    approvalDocStatusRepo.save(docStatus.updateNextApprover(approvers.get(currentSeq + 1)));
                    alarmToApprover(approvers.get(currentSeq + 1));   // 다음번 결재자가 있는 경우에는 해당 유저에게 sse로 알린다.

                } else {
                    approvalDocStatusRepo.save(docStatus.updateFinish(approvers.get(currentSeq)));
                    
                    // 결재가 완료되면 등록한 유저에게 sse로 알린다.
                    Document docs = docsService.findById(docsId);
                    alarmToSubmitter(docStatus.getWriterId(), status, docs.getId(), docs.getKind(), docs.getTitle());

                    if(docs.getKind().equals(DocumentKind.DAYOFF))   // 휴가 결재문서가 최종 승인되면 개인일정에 자동 등록.
                        scheduleService.saveScheduleForDayoff(docs, dayoffService.getSubDtoList(docs.getId()));
                }
            }

            if(status.equals(ApproverStatus.REJECTED)) {
                approvalDocStatusRepo.save(docStatus.updateFinish(approvers.get(currentSeq)));

                // 결재가 완료되면 등록한 유저에게 sse로 알린다.
                Document docs = docsService.findById(docsId);
                alarmToSubmitter(docStatus.getWriterId(), status, docs.getId(), docs.getKind(), docs.getTitle());
            }
        }
    }
    // ----- ----- ----- ----- ----- Approver and Referrer ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Sub List ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- Sub List Delete And Insert ----- ----- ----- ----- ----- //
    private RequestResult checkSubList(ApprovalDocsDTO dto, DocumentKind kind) {
        switch(kind) {
            case DAYOFF: return dayoffService.checkSubList((DayoffDocsDTO) dto);
            case PURCHASE: return purchaseService.checkSubList((PurchaseDocsDTO) dto);
            case OVERTIME: return overtimeService.checkSubList((OvertimeDocsDTO) dto);
            case NAMECARD: return namecardService.checkSubList((NamecardDocsDTO) dto);
            default: return RequestResult.getDefaultError("디테일 체크 에러입니다.");
        }
    }

    // 디테일 리스트가 있는 결재문서는 insert, update 할 때 기존의 모든 entity를 삭제 후 insert 한다.
    private void subListSave(ApprovalDocsDTO docsDTO, Document docs, DocumentKind kind) {
        switch(kind) {
            case DAYOFF: dayoffService.subListSave((DayoffDocsDTO) docsDTO, docs);
                break;
            case PURCHASE: purchaseService.subListSave((PurchaseDocsDTO) docsDTO, docs);
                break;
            case OVERTIME: overtimeService.subListSave((OvertimeDocsDTO) docsDTO, docs);
                break;
            case NAMECARD: namecardService.subListSave((NamecardDocsDTO) docsDTO, docs);
                break;
            default:
                break;
        }
    }

    public RequestResult insertForSubListApproval(ApprovalDocsDTO dto, DocumentKind kind) {
        RequestResult checkDetail = checkSubList(dto, kind);
        if(!checkDetail.getStatus().equals(ResultStatus.SUCCESS)) { return checkDetail; }

        RequestResult result = insertAndReturnResult(dto, kind);
        if(result.getStatus().equals(ResultStatus.SUCCESS)) {
            Document docs = docsService.findById((Long)result.getReturnObj());
            subListSave(dto, docs, kind);
        }
        return result;
    }

    public RequestResult updateForSubListApproval(ApprovalDocsDTO dto, DocumentKind kind) {
        RequestResult checkDetail = checkSubList(dto, kind);
        if(!checkDetail.getStatus().equals(ResultStatus.SUCCESS)) { return checkDetail; }

        RequestResult result = updateAndReturnResult(dto, kind);
        if(result.getStatus().equals(ResultStatus.SUCCESS)) {
            Document docs = docsService.findById(dto.getId());
            subListSave(dto, docs, kind);
        }
        return result;
    }
    // ----- ----- ----- ----- ----- Sub List Delete And Insert ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching sub list ----- ----- ----- ----- ----- //
    public List<DayoffDTO> searchDayoffDtoList(Long docsId) {   // ViewController에서 사용.
        return dayoffService.getSubDtoList(docsId);
    }

    public List<PurchaseDTO> searchPurchaseDtoList(Long docsId) {
        return purchaseService.getSubDtoList(docsId);
    }

    public List<OvertimeDTO> searchOvertimeDtoList(Long docsId) {
        return overtimeService.getSubDtoList(docsId);
    }

    public List<NamecardDTO> searchNamecardDtoList(Long docsId) {
        return namecardService.getSubDtoList(docsId);
    }
    // ----- ----- ----- ----- ----- Searching sub list ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Sub List ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
}
