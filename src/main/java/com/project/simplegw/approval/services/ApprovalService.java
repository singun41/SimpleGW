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

    // SubListService ?????????
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
        logger.info("???????????? ????????? ????????? ?????????????????????.");
        return Arrays.stream(DocumentKind.values()).filter(e ->
            !(e.equals(DocumentKind.NOTICE) || e.equals(DocumentKind.FREEBOARD) || e.equals(DocumentKind.MEETING) || e.equals(DocumentKind.ARCHIVE) || e.equals(DocumentKind.SUGGESTION))
        ).sorted(Comparator.comparing(DocumentKind::getTitle)).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = CacheConfig.CUSTOM_CACHE_MANAGER, cacheNames = CacheNames.APPROVER_ROLES)
    public List<ApproverRole> getApproverRoles() {
        logger.info("?????????, ????????? ???????????? ????????? ?????????????????????.");
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
                // ?????? ??? ???????????? ?????? ???????????? ?????? ?????????
                return approverRepo.countReceivedAprovalDocs(receiverId);

            case REFERRER:
                // ???????????? ?????? ????????? ?????? ???????????? ?????????
                return referrerRepo.countReceivedReferenceDocs(receiverId);

            default:
                return 0;
        }
    }

    // ???????????? ????????????
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

    // ????????????: ??? ????????? ???????????? ???????????? / ????????? ?????? ??????
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

    // ????????????: ??????/?????? ??????????????? ?????? ??????
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

    // ???????????? ????????????
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

    // ????????? ????????????
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
                return RequestResult.getDefaultFail("???????????? ????????? ???????????????.");
            }

            List<Long> approverList = new ArrayList<>(Arrays.asList(approvalLineDTO.getArrApprover()));
            List<Long> referrerList = new ArrayList<>(Arrays.asList(approvalLineDTO.getArrReferrer()));

            if(approverList.get(0).equals(ownerId)) {
                approverList.remove(0);   // ???????????? ?????? ????????? ??????????????? ??????. ??????????????? ????????? ????????????. ?????? ???????????? ?????????.
            }
            if(approverList.stream().filter(id -> id.equals(ownerId)).findFirst().isPresent()) {
                return RequestResult.getDefaultFail("??????????????? ??? ????????? ?????? ?????? ????????? ????????? ??? ????????????.");
            }
            if(referrerList.stream().filter(id -> id.equals(ownerId)).findFirst().isPresent()) {
                return RequestResult.getDefaultFail("???????????? ????????? ????????? ??? ????????????.");
            }
            if(approverList.size() != approverList.stream().distinct().count()) {
                return RequestResult.getDefaultFail("????????? ????????? ????????? ????????? ????????????.");
            }
            if(referrerList.size() != referrerList.stream().distinct().count()) {
                return RequestResult.getDefaultFail("????????? ????????? ????????? ????????? ????????????.");
            }

            List<TemplateLineMaster> savedList = lineMasterRepo.findAllByOwnerId(ownerId);
            if(approvalLineDTO.getMasterId() == null || approvalLineDTO.getMasterId().equals(Long.valueOf(0))) {   // ?????? ????????? ???
                if(savedList.stream().filter(m -> m.getTitle().equals(title)).findFirst().isPresent()) {
                    return RequestResult.getDefaultFail("???????????? ????????? ?????????????????????.");
                }
            }

            Optional<TemplateLineMaster> searchedMaster = savedList.stream().filter(m -> m.getId().equals(approvalLineDTO.getMasterId())).findFirst();
            TemplateLineMaster master;

            String resultMsg;
            if(searchedMaster.isPresent()) {   // ?????? ?????? ????????? ???????????? ?????? ??????(=????????????)
                master = searchedMaster.get();   // master entity??? ??????????????? ?????? ????????????

                List<TemplateLineDetails> originDetailsList = lineDetailsRepo.findAllByMasterId(approvalLineDTO.getMasterId());
                lineDetailsRepo.deleteAll(originDetailsList);
                lineDetailsRepo.flush();   // hibernate ?????? ????????? ?????? insert --> update --> delete ????????? ????????????. ????????? flush??? ????????? delete??? ?????? ????????????.
                
                resultMsg = Constants.RESULT_MESSAGE_UPDATED;

            } else {
                master = lineMasterRepo.save(TemplateLineMaster.builder().id(null).owner(memberService.searchMemberDetailsById(ownerId)).title(title).build());
                resultMsg = Constants.RESULT_MESSAGE_INSERTED;
            }

            List<TemplateLineDetails> detailsList = new ArrayList<>();
            // ???????????? ???????????? ?????????
            TemplateLineDetails first = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.SUBMITTER).seq(0).member(memberService.searchMemberDetailsById(ownerId)).build();
            detailsList.add(first);
    
            // ??????
            approverList.forEach(approver -> {
                TemplateLineDetails details = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.APPROVER).seq(approverList.indexOf(approver)+1).member(memberService.searchMemberDetailsById(approver)).build();
                detailsList.add(details);
            });
    
            // ??????
            referrerList.forEach(referrer -> {
                TemplateLineDetails details = TemplateLineDetails.builder().id(null).master(master).role(ApproverRole.REFERRER).seq(0).member(memberService.searchMemberDetailsById(referrer)).build();
                detailsList.add(details);
            });
    
            lineDetailsRepo.saveAll(detailsList);
            return RequestResult.getDefaultSuccess(resultMsg);

        } catch(Exception e) {
            e.printStackTrace();
            logger.warn("{}{}???????????? ????????? ?????? ????????? ?????????????????????.", e.getMessage(), System.lineSeparator());
            return RequestResult.getDefaultError("???????????? ????????? ?????? ????????? ?????????????????????.");
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
            return RequestResult.getDefaultFail("?????? ?????????????????????.");

        } else {
            if(target.getOwner().getId().equals(ownerId)) {
                lineMasterRepo.delete(target);
                return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);
            
            } else {
                return RequestResult.getDefaultFail("???????????? ???????????? ????????????.");
            }
        }
    }
    // ----- ----- ----- ----- ----- Line Template handling ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Line save ----- ----- ----- ----- ----- //
    private void saveApprovalLine(Long docsId, Long[] arrApprover, Long[] arrReferrer, boolean isRegistered) {
        Document docs = docsService.findById(docsId);
        
        // ?????? ????????? ?????? ??? ??????.
        approverRepo.deleteByDocsId(docsId);
        referrerRepo.deleteByDocsId(docsId);
        approverRepo.flush();
        referrerRepo.flush();

        ArrayList<Long> approverList = new ArrayList<>( Arrays.stream(arrApprover).distinct().collect(Collectors.toList()) );   // ????????????
        ArrayList<Long> referrerList = new ArrayList<>( Arrays.stream(arrReferrer).distinct().collect(Collectors.toList()) );   // ????????????

        ArrayList<Approver> approvers = new ArrayList<>();
        ArrayList<Referrer> referrers = new ArrayList<>();
        
        MemberDetails writerDetails = docs.getMember();
        if(approverList.get(0).equals(writerDetails.getId())) {   // ????????? ????????? ????????? ????????? ????????? ??????. ??????: ??????????????? ?????? ???????????? ???????????? ?????????.
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
        // ??????????????? ??????????????? ????????????.
        // approverCount??? ???????????? ???????????? ??????????????? ????????? -1 ?????????.
        ApprovalDocStatus docStatus = ApprovalDocStatus.builder().docs(docs)
                                        .writerId(approvers.get(0).getApprover().getId()).approverCount(approvers.size() - 1)
                                        .status(ApproverStatus.PROCEED)
                                        .build();
        docStatus.updateNextApprover(approvers.get(1));
        approvalDocStatusRepo.save(docStatus);

        if(isRegistered) {   // ??????????????? ?????? ????????? ????????? ????????? ????????? ????????????.
            // ?????? ?????? ??????????????? sse??? ??????.
            alarmToApprover(approvers.get(1));

            // ????????? ???????????? ?????? ???????????? sse??? ??????.
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
            new StringBuilder("??????: ").append(docsId.toString()).append(", ??????: \"").append(title).append("\" ????????? ").append(status.getTitle()).append(" ???????????????.").toString()
        );
    }
    // ----- ----- ----- ----- ----- Sse Alarm  ----- ----- ----- ----- ----- //

    

    // ----- ----- ----- ----- ----- approval common method ----- ----- ----- ----- ----- //
    // insert, update, delete??? DocsService??? ???????????? ????????????.
    // ????????? ??? ?????? ???????????? ??? ???????????? ??? ????????? ?????? ???????????? DocsService??? ?????????.
    private boolean isEmptyApproverLine(Long[] approvers) {
        return (approvers.length == 0);
    }

    private String setApprovalContent(DocumentKind kind, String content) {
        String returnString = null;
        // DocsService??? insertAndReturnResult??? ????????????.
        // ?????? ????????? ?????? Content ???????????? ???????????? ?????? ?????? ????????? ?????? ???????????? DocsService??? isEmptyContent?????? ????????? ????????? ??????.
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

    public boolean isProceedDocs(Long docsId) {   // viewController ????????? ?????? ???????????? ?????? public?????? ??????
        if(docsId == null)
            return false;
        
        ApprovalDocStatus docsStatus = approvalDocStatusRepo.getByDocsId(docsId);
        if(docsStatus == null)
            return false;
        
        else
            if(docsStatus.isFinished())
                return true;
            
            else if(docsStatus.getApproverSeq() > 1)   // 1?????? ?????? --> ?????? ???????????? ????????? ?????????????????? ???????????? ?????????. ??????, ?????? ??????.
                return true;

            else if(docsStatus.getApproverSeq() == 1 && (docsStatus.getStatus().equals(ApproverStatus.CONFIRMED) || docsStatus.getStatus().equals(ApproverStatus.REJECTED)))   // ?????? ????????? ????????? ?????? --> ??????????????? ?????? ????????? 1?????? ?????? ????????? ???????????? ????????? CONFIRMED or REJECTED??? ??????.
                return true;
            
            else
                return false;
    }
    // ----- ----- ----- ----- ----- approval common method ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Insert ----- ----- ----- ----- ----- //
    public RequestResult insertAndReturnResult(ApprovalDocsDTO approvalDocsDTO, DocumentKind kind) {
        if(isEmptyApproverLine(approvalDocsDTO.getApprovers()))   //??????????????? ??????. ????????? ????????? ???????????? ???????????? ??????.
            return RequestResult.getDefaultFail("??????????????? ???????????????.");

        if(isProceedDocs(approvalDocsDTO.getId()))
            return RequestResult.getDefaultFail("????????? ???????????? ????????? ??? ????????????.");

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
        if(isEmptyApproverLine(approvalDocsDTO.getApprovers()))   //??????????????? ??????. ????????? ????????? ???????????? ???????????? ??????.
            return RequestResult.getDefaultFail("??????????????? ???????????????.");

        if(isProceedDocs(approvalDocsDTO.getId()))
            return RequestResult.getDefaultFail("????????? ???????????? ????????? ??? ????????????.");

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
            return RequestResult.getDefaultFail("????????? ???????????? ????????? ??? ????????????.");
        
        return docsService.deleteDocs(docsId, memberId, DocumentType.APPROVAL, kind);
    }
    // ----- ----- ----- ----- ----- Delete ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Approver and Referrer ----- ----- ----- ----- ----- //
    @Async   // ??????????????? ???????????? ????????? ?????? ???????????? ??? ??? ??????????????? ??????????????????.
    public void updateReferrerChecked(Long docsId, Long referrerId) {
        List<Referrer> referrers = referrerRepo.findAllByDocsId(docsId);
        if(referrers == null) return;

        Optional<Referrer> result = referrers.stream().filter(elem -> elem.getReferrer().getId().equals(referrerId)).findFirst();
        if(result.isPresent()) {
            Referrer referrer = result.get();

            if(referrer.getCheckedDatetime() == null) {   // ???????????? ?????? ?????? ?????? 1?????? ??????????????????.
                referrerRepo.save(referrer.updateCheckedDatetime());
            }
        }
    }

    @Async   // ???????????? ?????? ?????? ?????? ??????.
    public void updateApproverStatus(Long docsId, Long approverId, ApproverStatus status) {
        List<Approver> approvers = approverRepo.findAllByDocsId(docsId);
        Optional<Approver> result = approvers.stream().filter(elem -> elem.getApprover().getId().equals(approverId)).findFirst();

        if(result.isPresent()) {   // ?????? ????????? ????????? ????????? ????????? ???????????? ??????. ????????? api??? ????????? ?????? ????????? ????????? null????????? ???????????? ?????????.
            Approver currentApprover = result.get();
            approverRepo.save(currentApprover.updateStatus(status));

            ApprovalDocStatus docStatus = approvalDocStatusRepo.getByDocsId(docsId);
            int currentSeq = approvers.indexOf(currentApprover);

            if(status.equals(ApproverStatus.CONFIRMED)) {
                if(currentSeq < approvers.size() - 1) {

                    Approver nextApprover = approvers.get(currentSeq + 1);
                    approverRepo.save(nextApprover.updateStatusToProceed());

                    approvalDocStatusRepo.save(docStatus.updateNextApprover(approvers.get(currentSeq + 1)));
                    alarmToApprover(approvers.get(currentSeq + 1));   // ????????? ???????????? ?????? ???????????? ?????? ???????????? sse??? ?????????.

                } else {
                    approvalDocStatusRepo.save(docStatus.updateFinish(approvers.get(currentSeq)));
                    
                    // ????????? ???????????? ????????? ???????????? sse??? ?????????.
                    Document docs = docsService.findById(docsId);
                    alarmToSubmitter(docStatus.getWriterId(), status, docs.getId(), docs.getKind(), docs.getTitle());

                    if(docs.getKind().equals(DocumentKind.DAYOFF))   // ?????? ??????????????? ?????? ???????????? ??????????????? ?????? ??????.
                        scheduleService.saveScheduleForDayoff(docs, dayoffService.getSubDtoList(docs.getId()));
                }
            }

            if(status.equals(ApproverStatus.REJECTED)) {
                approvalDocStatusRepo.save(docStatus.updateFinish(approvers.get(currentSeq)));

                // ????????? ???????????? ????????? ???????????? sse??? ?????????.
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
            default: return RequestResult.getDefaultError("????????? ?????? ???????????????.");
        }
    }

    // ????????? ???????????? ?????? ??????????????? insert, update ??? ??? ????????? ?????? entity??? ?????? ??? insert ??????.
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
    public List<DayoffDTO> searchDayoffDtoList(Long docsId) {   // ViewController?????? ??????.
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
