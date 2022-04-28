package com.project.simplegw.approval.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.project.simplegw.approval.dtos.ApprovalDocsDTO;
import com.project.simplegw.approval.dtos.ApprovalLineDTO;
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
import com.project.simplegw.approval.dtos.TemplateLineDetailsDTO;
import com.project.simplegw.approval.dtos.TemplateLineMasterDTO;
import com.project.simplegw.approval.vos.ApproverRole;
import com.project.simplegw.approval.vos.ApproverStatus;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval")
public class ApprovalController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }


    // ----- ----- ----- ----- ----- Line Template ----- ----- ----- ----- ----- //
    @PostMapping(path = "/line-template")
    public ResponseEntity<Object> insertApprovalLineTemplate(@RequestBody ApprovalLineDTO approvalLineDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.approvalLineTemplateSave(approvalLineDTO, loginUser.getMember().getId()));
    }

    @GetMapping(path = "/line-template/master-list")
    public List<TemplateLineMasterDTO> getSavedTemplateLineMasterList(@AuthenticationPrincipal SecurityUser loginUser) {
        return approvalService.getSavedTemplateLineMasterList(loginUser.getMember().getId());
    }

    @GetMapping(path = "/line-template/details-list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TemplateLineDetailsDTO getSavedTemplateLineDetailsList(@RequestParam Long masterId) {
        return approvalService.getSavedTemplateLineDetailsList(masterId);
    }

    @DeleteMapping(path = "/line-template/{masterId}")
    public ResponseEntity<Object> deleteApprovalLineTemplate(@PathVariable Long masterId, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.deleteTemplateLine(loginUser.getMember().getId(), masterId));
    }
    // ----- ----- ----- ----- ----- Line Template ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Saved line searching ----- ----- ----- ----- ----- //
    @GetMapping(path = "/line/{docsId}")
    public Map<String, Object> getSavedLine(@PathVariable(name = "docsId") Long docsId) {
        return approvalService.getSavedLine(docsId);
    }
    // ----- ----- ----- ----- ----- Saved line searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Counting ----- ----- ----- ----- ----- //
    @GetMapping(path = "/count/proceeding")
    public long getProceedingDocsCount(@AuthenticationPrincipal SecurityUser loginUser) {
        return approvalService.proceedingDocsCount(loginUser.getMember().getId());
    }

    @GetMapping(path = "/count/received/{role}")
    public long getReceivedCountForApprover(@PathVariable(name = "role") ApproverRole role, @AuthenticationPrincipal SecurityUser loginUser) {
        try {
            return approvalService.getReceivedApprovalDocsCount(role, loginUser.getMember().getId());
        } catch(Exception e) {
            logger.warn("{}{}결재문서 수신 카운트 PathVariable 파라미터가 잘못되었습니다. 잘못 입력된 파라미터: {}", e.getMessage(), System.lineSeparator(), role);
            return 0;
        }
    }
    // ----- ----- ----- ----- ----- Counting ----- ----- ----- ----- ----- //
    


    // ----- ----- ----- ----- ----- List searching ----- ----- ----- ----- ----- //
    // 결재요청 문서 리스트
    @GetMapping(path = "/received/list/current", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<ReceivedDocsDTO> getReceivedDocsList(@AuthenticationPrincipal SecurityUser loginUser, @RequestParam ApproverRole type, @RequestParam DocumentKind kind) {
        return approvalService.getReceivedDocsList(type, loginUser.getMember().getId(), kind);
    }
    
    // 수신문서 리스트
    @GetMapping(path = "/received/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<ReceivedDocsDTO> getReceivedDocsList(@AuthenticationPrincipal SecurityUser loginUser, @RequestParam ApproverRole type, @RequestParam DocumentKind kind,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd) {
        return approvalService.getReceivedDocsList(type, loginUser.getMember().getId(), kind, dateStart, dateEnd);
    }
    

    // 진행중인 문서 리스트
    @GetMapping(path = "/proceeding/list")
    public List<DocsApprovalDTO> getProceedingDocs(@AuthenticationPrincipal SecurityUser loginUser) {
        return approvalService.getProceedingDocs(loginUser.getMember().getId());
    }

    // 완결된 문서 리스트
    @GetMapping(path = "/finished/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> getFinishedList(@AuthenticationPrincipal SecurityUser loginUser,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd) {
        return approvalService.getFinishedList(loginUser.getMember().getId(), dateStart, dateEnd);
    }


    // 본인이 작성한 결재문서 종류별 리스트를 기간으로 검색
    private List<DocsApprovalDTO> getDocsApprovalDtoList(DocumentKind kind, LocalDate dateStart, LocalDate dateEnd, boolean isRegistered, Long memberId) {
        DocsSearchDTO searchConditions = new DocsSearchDTO();
        searchConditions.setType(DocumentType.APPROVAL).setKind(kind).setDateStart(dateStart).setDateEnd(dateEnd).setRegistered(isRegistered);
        return approvalService.getDocsApprovalDtoList(searchConditions, memberId);
    }
    // ----- ----- ----- ----- ----- List searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Approver confirmed or rejected ----- ----- ----- ----- ----- //
    @PutMapping(path = "/confirmed", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> confirmed(@RequestParam Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        approvalService.updateApproverStatus(docsId, loginUser.getMember().getId(), ApproverStatus.CONFIRMED);
        return ResponseEntityConverter.getFromRequestResult(RequestResult.getDefaultSuccess("승인하였습니다."));
    }
    @PutMapping(path = "/rejected", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> rejected(@RequestParam Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        approvalService.updateApproverStatus(docsId, loginUser.getMember().getId(), ApproverStatus.REJECTED);
        return ResponseEntityConverter.getFromRequestResult(RequestResult.getDefaultSuccess("반려하였습니다."));
    }
    // ----- ----- ----- ----- ----- Approver confirmed or rejected ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- default-report ----- ----- ----- ----- ----- //
    @GetMapping(path = "/default-report/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchDefaultReportList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.DEFAULT_REPORT, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/default-report")
    public ResponseEntity<Object> insertDefaultReport(@RequestBody ApprovalDocsDTO approvalDocsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.insertAndReturnResult(approvalDocsDTO.setWriterId(loginUser.getMember().getId()), DocumentKind.DEFAULT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    @PutMapping(path = "/default-report")
    public ResponseEntity<Object> updateDefaultReport(@RequestBody ApprovalDocsDTO approvalDocsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.updateAndReturnResult(approvalDocsDTO.setWriterId(loginUser.getMember().getId()), DocumentKind.DEFAULT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    @DeleteMapping(path = "/default-report/{docsId}")
    public ResponseEntity<Object> deleteDefaultReport(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.DEFAULT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- default-report ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- incident-report ----- ----- ----- ----- ----- //
    @GetMapping(path = "/incident-report/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchIncidentReportList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.INCIDENT_REPORT, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/incident-report")
    public ResponseEntity<Object> insertIncidentReport(@RequestBody ApprovalDocsDTO approvalDocsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.insertAndReturnResult(approvalDocsDTO.setWriterId(loginUser.getMember().getId()), DocumentKind.INCIDENT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    @PutMapping(path = "/incident-report")
    public ResponseEntity<Object> updateIncidentReport(@RequestBody ApprovalDocsDTO approvalDocsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.updateAndReturnResult(approvalDocsDTO.setWriterId(loginUser.getMember().getId()), DocumentKind.INCIDENT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    @DeleteMapping(path = "/incident-report/{docsId}")
    public ResponseEntity<Object> deleteIncidentReport(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.INCIDENT_REPORT);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- incident-report ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //
    @GetMapping(path = "/dayoff/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchDayoffList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.DAYOFF, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/dayoff")
    public ResponseEntity<Object> insertDayoff(@RequestBody DayoffDocsDTO dayoffDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.insertForSubListApproval(dayoffDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.DAYOFF));
    }
    @GetMapping(path = "/dayoff/details/{docsId}")
    public List<DayoffDTO> searchDayoffDetails(@PathVariable Long docsId) {
        return approvalService.searchDayoffDtoList(docsId);
    }
    @PutMapping(path = "/dayoff")
    public ResponseEntity<Object> updateDayoff(@RequestBody DayoffDocsDTO dayoffDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.updateForSubListApproval(dayoffDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.DAYOFF));
    }
    @DeleteMapping(path = "/dayoff/{docsId}")
    public ResponseEntity<Object> deleteDayoff(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.DAYOFF);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- purchase ----- ----- ----- ----- ----- //
    @GetMapping(path = "/purchase/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchPurchaseList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.PURCHASE, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/purchase")
    public ResponseEntity<Object> insertPurchase(@RequestBody PurchaseDocsDTO purchaseDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.insertForSubListApproval(purchaseDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.PURCHASE));
    }
    @GetMapping(path = "/purchase/details/{docsId}")
    public List<PurchaseDTO> searchPurchaseDetails(@PathVariable Long docsId) {
        return approvalService.searchPurchaseDtoList(docsId);
    }
    @PutMapping(path = "/purchase")
    public ResponseEntity<Object> updatePurchase(@RequestBody PurchaseDocsDTO purchaseDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.updateForSubListApproval(purchaseDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.PURCHASE));
    }
    @DeleteMapping(path = "/purchase/{docsId}")
    public ResponseEntity<Object> deletePurchase(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.PURCHASE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- purchase ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- overtime ----- ----- ----- ----- ----- //
    @GetMapping(path = "/overtime/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchOvertimeList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.OVERTIME, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/overtime")
    public ResponseEntity<Object> insertOvertime(@RequestBody OvertimeDocsDTO overtimeDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.insertForSubListApproval(overtimeDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.OVERTIME));
    }
    @GetMapping(path = "/overtime/details/{docsId}")
    public List<OvertimeDTO> searchOvertimeDetails(@PathVariable Long docsId) {
        return approvalService.searchOvertimeDtoList(docsId);
    }
    @PutMapping(path = "/overtime")
    public ResponseEntity<Object> updateOvertime(@RequestBody OvertimeDocsDTO overtimeDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.updateForSubListApproval(overtimeDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.OVERTIME));
    }
    @DeleteMapping(path = "/overtime/{docsId}")
    public ResponseEntity<Object> deleteOvertime(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.OVERTIME);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- overtime ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- namecard ----- ----- ----- ----- ----- //
    @GetMapping(path = "/namecard/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsApprovalDTO> searchNamecardList(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd, @AuthenticationPrincipal SecurityUser loginUser
    ) {
        return getDocsApprovalDtoList(DocumentKind.NAMECARD, dateStart, dateEnd, true, loginUser.getMember().getId());
    }
    @PostMapping(path = "/namecard")
    public ResponseEntity<Object> insertNamecard(@RequestBody NamecardDocsDTO namecardDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.insertForSubListApproval(namecardDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.NAMECARD));
    }
    @GetMapping(path = "/namecard/details/{docsId}")
    public NamecardDTO searchNamecardDetails(@PathVariable Long docsId) {
        return approvalService.searchNamecardDtoList(docsId).get(0);   // 저장된 데이터는 항상 1건이므로.
    }
    @PutMapping(path = "/namecard")
    public ResponseEntity<Object> updateNamecard(@RequestBody NamecardDocsDTO namecardDocs, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(approvalService.updateForSubListApproval(namecardDocs.setWriterId(loginUser.getMember().getId()), DocumentKind.NAMECARD));
    }
    @DeleteMapping(path = "/namecard/{docsId}")
    public ResponseEntity<Object> deleteNamecard(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = approvalService.deleteAndReturnResult(docsId, loginUser.getMember().getId(), DocumentKind.NAMECARD);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- namecard ----- ----- ----- ----- ----- //
}
