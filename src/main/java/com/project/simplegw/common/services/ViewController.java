package com.project.simplegw.common.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.project.simplegw.approval.services.ApprovalService;
import com.project.simplegw.common.dtos.BasecodeDTO;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.services.AttachmentsService;
import com.project.simplegw.document.services.DocsService;
import com.project.simplegw.document.services.DocsShareService;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.member.dtos.MemberDTO;
import com.project.simplegw.member.dtos.MemberInfoDTO;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.schedule.dtos.ScheduleDTO;
import com.project.simplegw.schedule.services.ScheduleService;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.security.SecurityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {
	
	private final BasecodeService basecodeService;
	private final MemberService memberService;
	private final DocsService docsService;
	private final DocsShareService docsShareService;
	private final AttachmentsService attachmentsService;
	private final ApprovalService approvalService;
	private final ScheduleService scheduleService;

	@Autowired
	public ViewController(
		MemberService memberService, DocsService docsService, DocsShareService docsShareService, AttachmentsService attachmentsService,
		BasecodeService basecodeService, ApprovalService approvalService, ScheduleService scheduleService
	) {
		this.basecodeService = basecodeService;
		this.memberService = memberService;
		this.docsService = docsService;
		this.docsShareService = docsShareService;
		this.attachmentsService = attachmentsService;
		this.approvalService = approvalService;
		this.scheduleService = scheduleService;
	}


    // 웹크롤러에 의한 검색 방지, text/plain, UTF-8 설정을 해줘야 browser에서 라인피드가 먹는다.
    @GetMapping(value="/robots.txt", produces="text/plain;charset=UTF-8")
    @ResponseBody
    public String robots() { return "User-agent: *" + System.lineSeparator() + "Disallow: /"; }

    @RequestMapping("/login")   // login 할 때와 login 페이지로 이동할 때 모두 사용하므로 RequestMapping으로 처리.
	public String login() { return "login"; }

	@GetMapping("/logout")
	public void logout() { }
	
	@GetMapping("/error/400")
	public void badRequest() { }

	@GetMapping("/error/403")
	public void accessDenied() { }
	
	@GetMapping("/error/404")
	public void notFound() { }

	@GetMapping("/error/410")
	public void notExists() { }
	
	@GetMapping("/sessionExpired")
	public String sessionExpired() { return "sessionExpired"; }
	
	@GetMapping(value = "/")
	public String indexRedirect() { return "redirect:frame"; }

	@GetMapping("/frame")
	public String defaultPage(@AuthenticationPrincipal SecurityUser loginUser, Model model, HttpServletRequest request) {
		if(request.getHeader("User-Agent").toUpperCase().contains("MOBI"))
			return "mobile/main/main";

		model.addAttribute("myInfo", memberService.getMemberInfo(loginUser.getMember().getId()))
			.addAttribute( "pwUpdateGuide", new StringBuilder("보안을 위해 ").append(Constants.PW_UPDATE_AT_LEAST_LENGTH)
				.append("자 이상, 영문자, 숫자, 특수문자를 포함해 작성하세요.").toString() );
		return "main/frame";
	}

	@GetMapping("/main")
	public String mainPage(HttpServletRequest request) {
		if(request.getHeader("User-Agent").toUpperCase().contains("MOBI"))
			return "mobile/main";
		
		return "main/main";
	}

	
	// ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- //
	@GetMapping("/admin/member/listpage")
	public String memberListPage(Model model) {
		model.addAttribute("role", MemberRole.values()).addAttribute("jobTitle", basecodeService.getCodeList(BasecodeType.JOB_TITLE));
		return "admin/member-list";
	}
	@GetMapping("/admin/code/config")
	public String codeConfigPage(Model model) {
		model.addAttribute("types", basecodeService.getAllTypes());
		return "admin/code-config";
	}
	// ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- BOARD ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
	// ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //
	@GetMapping("/notice/listpage")
	public String noticeListPage() {
		return "board/notice/notice-list";
	}

	@GetMapping("/notice/writepage")
	public String noticeWritePage() {
		return "board/notice/notice-write";
	}

	@GetMapping(path = "/notice/{docsId}")
	public String noticeView(@PathVariable(name = "docsId") Long docsId, Model model) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.NOTICE);
		if(docsDTO == null) {
			return Constants.ERROR_PAGE_410;
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "board/notice/notice-view";
	}

	@GetMapping(path = "/notice/modifypage/{docsId}")
	public String noticeModifyPage(@PathVariable(name = "docsId") Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.NOTICE);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		// 권한이 있는 멤버들이 공지를 수정해야 할 필요가 있어 여기서는 작성자를 체크하지 않음.
		// if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
		// 	return Constants.ERROR_PAGE_403;

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "board/notice/notice-modify";
	}
	// ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- //
	@GetMapping("/freeboard/listpage")
	public String freeboardListPage() {
		return "board/freeboard/freeboard-list";
	}

	@GetMapping("/freeboard/writepage")
	public String freeboardWritePage() {
		return "board/freeboard/freeboard-write";
	}

	@GetMapping(path = "/freeboard/{docsId}")
	public String freeboardView(@PathVariable(name = "docsId") Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.FREEBOARD);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		model.addAttribute("docs", docs).addAttribute("fileList", attachmentsService.getFileList(docsId));

		if(docs.getMemberId().equals(loginUser.getMember().getId())) {
			// writer --> full
			model.addAttribute("updatable", true).addAttribute("deletable", true);

		} else if(loginUser.getMember().getRole().equals(MemberRole.MANAGER) || loginUser.getMember().getRole().equals(MemberRole.ADMIN)) {
			// admin, manager --> delete only
			model.addAttribute("updatable", false).addAttribute("deletable", true);

		} else {
			// others --> not authorized
			model.addAttribute("updatable", false).addAttribute("deletable", false);
		}

		return "board/freeboard/freeboard-view";
	}

	@GetMapping(path = "/freeboard/modifypage/{docsId}")
	public String freeboardModifyPage(@PathVariable(name = "docsId") Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.FREEBOARD);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "board/freeboard/freeboard-modify";
	}
	// ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- meeting-minutes ----- ----- ----- ----- ----- //
	@GetMapping(path = "/meeting/listpage")
	public String meetingMinutesListPage() {
		return "board/meeting-minutes/meeting-minutes-list";
	}
	@GetMapping(path = "/meeting/writepage")
	public String meetingMinutesWritePage() {
		return "board/meeting-minutes/meeting-minutes-write";
	}
	@GetMapping(path = "/meeting/{docsId}")
	public String meetingMinutesView(@PathVariable(name = "docsId") Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.MEETING);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		docsShareService.updateReferrerChecked(docsId, loginUser.getMember().getId());

		// writer --> full, others --> not authorized
		boolean isWriter = docs.getMemberId().equals(loginUser.getMember().getId());
		model.addAttribute("docs", docs).addAttribute("fileList", attachmentsService.getFileList(docsId)).addAttribute("updatable", isWriter).addAttribute("deletable", isWriter);

		return "board/meeting-minutes/meeting-minutes-view";
	}
	@GetMapping(path = "/meeting/modifypage/{docsId}")
	public String meetingMinutesModifyPage(@PathVariable(name = "docsId") Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.MEETING);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "board/meeting-minutes/meeting-minutes-modify";
	}
	// ----- ----- ----- ----- ----- meeting-minutes ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- //
	@GetMapping(path = "/archive/listpage")
	public String archiveListPage() {
		return "board/archive/archive-list";
	}

	@GetMapping(path = "/archive/writepage")
	public String archiveWritePage() {
		return "board/archive/archive-write";
	}

	@GetMapping(path = "/archive/{docsId}")
	public String archiveView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.ARCHIVE);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		model.addAttribute("docs", docs).addAttribute("fileList", attachmentsService.getFileList(docsId));

		if(docs.getMemberId().equals(loginUser.getMember().getId()) || loginUser.getMember().getRole().equals(MemberRole.MANAGER) || loginUser.getMember().getRole().equals(MemberRole.ADMIN)) {
			// writer, admin, manager  --> full
			model.addAttribute("updatable", true).addAttribute("deletable", true);

		} else {
			// others --> not authorized
			model.addAttribute("updatable", false).addAttribute("deletable", false);
		}

		return "board/archive/archive-view";
	}

	public String archiveModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.BOARD, DocumentKind.ARCHIVE);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		// 잘못된 내용이 있을 경우 권한이 있는 멤버들이 수정해야 할 필요가 있어 여기서는 작성자를 체크하지 않음.
		// if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
		// 	return Constants.ERROR_PAGE_403;

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));

		return "board/archive/archive-modify";
	}
	// ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- //
	@GetMapping(path = "/daily-work-record")
	public String workRecordPage() {
		return "work/work-record";
	}

	@GetMapping(path = "/daily-work-record-listpage")
	public String workRecordListPage(@AuthenticationPrincipal SecurityUser loginUser, Model model) {
		if(loginUser.getMember().getRole().equals(MemberRole.USER) || loginUser.getMember().getRole().equals(MemberRole.MANAGER)) {
			return Constants.ERROR_PAGE_403;
		} else if(loginUser.getMember().getRole().equals(MemberRole.LEADER)) {
			model.addAttribute("role", loginUser.getMember().getRole());
		} else {
			model.addAttribute("role", loginUser.getMember().getRole()).addAttribute("teamList", memberService.getTeamList());
		}
		return "work/work-record-list";
	}
	// ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- temporary saved list ----- ----- ----- ----- ----- //
	@GetMapping(path = "/board/temporary/listpage")
	public String temporaryListPage() {
		return "board/common/temporary-list";
	}
	// ----- ----- ----- ----- ----- temporary saved list ----- ----- ----- ----- ----- //
	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- BOARD ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //




	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Common - share ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
	@GetMapping(path = "/docs/share")
	public String docsSharePage(Model model) {
		model.addAttribute("teamList", memberService.getTeamList());
		return "board/common/docs-share";
	}
	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Common - share ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- member list info ----- ----- ----- ----- ----- //
	@GetMapping(path = "/members/listpage")
	public String memberInfoListPage(Model model) {
		model.addAttribute("teamList", memberService.getTeamList());
		return "members/member-list";
	}
	// ----- ----- ----- ----- ----- member list info ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- APPROVAL ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
	// ----- ----- ----- ----- ----- Received list page ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/received/listpage")
	public String approvalReceivedListPage(Model model) {
		model.addAttribute("approverRoles", approvalService.getApproverRoles()).addAttribute("kindList", approvalService.getApprovalKinds());
		return "approval/common/received-list";
	}
	@GetMapping(path = "/approval/received/current/approver")
	public String approvalReceivedListPageForCurrentApprover() {
		return "approval/common/received-list-approver";
	}
	@GetMapping(path = "/approval/received/current/referrer")
	public String approvalReceivedListPageForCurrentReferrer() {
		return "approval/common/received-list-referrer";
	}
	// ----- ----- ----- ----- ----- Received list page ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- Submitted list page ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/proceeding/listpage")
	public String approvalSubmittedProceedingListPage(Model model) {
		model.addAttribute("isFinished", false);
		return "approval/common/submitted-proceeding-list";
	}
	@GetMapping(path = "/approval/finished/listpage")
	public String approvalSubmittedFinishedListPage(Model model) {
		model.addAttribute("isFinished", true);
		return "approval/common/submitted-finished-list";
	}
	// ----- ----- ----- ----- ----- Submitted list page ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- Line template page ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/line")
	public String approvalLinePage(Model model) {
		model.addAttribute("teamList", memberService.getTeamList());
		return "approval/common/approval-line";
	}
	// ----- ----- ----- ----- ----- Line template page ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- all approval docs common ----- ----- ----- ----- ----- //
	private void checkReferrerDatetime(Long docsId, Long memberId) {
		approvalService.updateReferrerChecked(docsId, memberId);
	}
	private void getModelForApprovalDocs(Model model, DocsDTO docs, Long memberId) {
		boolean isWriter = docs.getMemberId().equals(memberId);
		
		// 진행중인 문서 true --> 수정, 삭제 불가.
		if(approvalService.isProceedDocs(docs.getId())) {
			model.addAttribute("docs", docs).addAttribute("fileList", attachmentsService.getFileList(docs.getId()))
				.addAttribute("isWriter", isWriter).addAttribute("updatable", false).addAttribute("deletable", false)
				.addAttribute("currentApprover", approvalService.isCurrentApprover(docs.getId(), memberId));
		} else {
			model.addAttribute("docs", docs).addAttribute("fileList", attachmentsService.getFileList(docs.getId()))
				.addAttribute("isWriter", isWriter).addAttribute("updatable", isWriter).addAttribute("deletable", isWriter)
				.addAttribute("currentApprover", approvalService.isCurrentApprover(docs.getId(), memberId));
		}
	}
	// ----- ----- ----- ----- ----- all approval docs common ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- default-report ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/default-report/listpage")
	public String defaultReportListPage() {
		return "approval/default-report/default-report-list";
	}
	@GetMapping(path = "/approval/default-report/writepage")
	public String defaultReportWritePage() {
		return "approval/default-report/default-report-write";
	}
	@GetMapping(path = "/approval/default-report/{docsId}")
	public String defaultReportView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.DEFAULT_REPORT);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		return "approval/default-report/default-report-view";
	}
	@GetMapping(path = "/approval/default-report/modifypage/{docsId}")
	public String defaultReportModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.DEFAULT_REPORT);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;
		
		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "approval/default-report/default-report-modify";
	}
	// ----- ----- ----- ----- ----- default-report ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- incident-report ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/incident-report/listpage")
	public String incidentReportListPage() {
		return "approval/incident-report/incident-report-list";
	}
	@GetMapping(path = "/approval/incident-report/writepage")
	public String incidentReportWritePage() {
		return "approval/incident-report/incident-report-write";
	}
	@GetMapping(path = "/approval/incident-report/{docsId}")
	public String incidentReportView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.INCIDENT_REPORT);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		return "approval/incident-report/incident-report-view";
	}
	@GetMapping(path = "/approval/incident-report/modifypage/{docsId}")
	public String incidentReportModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.INCIDENT_REPORT);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;
		
		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "approval/incident-report/incident-report-modify";
	}
	// ----- ----- ----- ----- ----- incident-report ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/dayoff/listpage")
	public String dayoffListPage() {
		return "approval/dayoff/dayoff-list";
	}
	@GetMapping(path = "/approval/dayoff/writepage")
	public String dayoffWritePage(Model model) {
		model.addAttribute("dayoffList", basecodeService.getCodeList(BasecodeType.DAYOFF));
		return "approval/dayoff/dayoff-write";
	}
	@GetMapping(path = "/approval/dayoff/{docsId}")
	public String dayoffView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.DAYOFF);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		model.addAttribute("dayoffList", approvalService.searchDayoffDtoList(docsId));
		
		return "approval/dayoff/dayoff-view";
	}
	@GetMapping(path = "/approval/dayoff/modifypage/{docsId}")
	public String dayoffModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.DAYOFF);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;

		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		model.addAttribute("dayoffList", basecodeService.getCodeList(BasecodeType.DAYOFF));
		return "approval/dayoff/dayoff-modify";
	}
	// ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- purchase ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/purchase/listpage")
	public String purchaseListPage() {
		return "approval/purchase/purchase-list";
	}
	@GetMapping(path = "/approval/purchase/writepage")
	public String purchaseWritePage() {
		return "approval/purchase/purchase-write";
	}
	@GetMapping(path = "/approval/purchase/{docsId}")
	public String purchaseView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.PURCHASE);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		model.addAttribute("purchaseList", approvalService.searchPurchaseDtoList(docsId));

		return "approval/purchase/purchase-view";
	}
	@GetMapping(path = "/approval/purchase/modifypage/{docsId}")
	public String purchaseModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.PURCHASE);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;

		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "approval/purchase/purchase-modify";
	}
	// ----- ----- ----- ----- ----- purchase ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- overtime ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/overtime/listpage")
	public String overtimeListPage() {
		return "approval/overtime/overtime-list";
	}
	@GetMapping(path = "/approval/overtime/writepage")
	public String overtimeWritePage(Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		List<MemberDTO> memberList = memberService.getTeamMembers(loginUser.getMember().getId());
		List<BasecodeDTO> overtimeList = basecodeService.getCodeList(BasecodeType.OVERTIME);
		model.addAttribute("memberList", memberList).addAttribute("overtimeList", overtimeList);
		return "approval/overtime/overtime-write";
	}
	@GetMapping(path = "/approval/overtime/{docsId}")
	public String overtimeView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.OVERTIME);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		model.addAttribute("overtimeList", approvalService.searchOvertimeDtoList(docsId));

		return "approval/overtime/overtime-view";
	}
	@GetMapping(path = "/approval/overtime/modifypage/{docsId}")
	public String overtimeModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.OVERTIME);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;
		
		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		model.addAttribute("overtimeList", basecodeService.getCodeList(BasecodeType.OVERTIME));
		model.addAttribute("memberList", memberService.getTeamMembers(loginUser.getMember().getId()));
		return "approval/overtime/overtime-modify";
	}
	// ----- ----- ----- ----- ----- overtime ----- ----- ----- ----- ----- //


	// ----- ----- ----- ----- ----- namecard ----- ----- ----- ----- ----- //
	@GetMapping(path = "/approval/namecard/listpage")
	public String namecardListPage() {
		return "approval/namecard/namecard-list";
	}
	@GetMapping(path = "/approval/namecard/writepage")
	public String namecardWritePage(Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		MemberInfoDTO myInfo = memberService.getMemberInfo(loginUser.getMember().getId());
		model.addAttribute("myInfo", myInfo);
		return "approval/namecard/namecard-write";
	}
	@GetMapping(path = "/approval/namecard/{docsId}")
	public String namecardView(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docs = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.NAMECARD);
		if(docs == null) {
			return Constants.ERROR_PAGE_410;
		}

		Long memberId = loginUser.getMember().getId();

		checkReferrerDatetime(docsId, memberId);
		getModelForApprovalDocs(model, docs, memberId);
		model.addAttribute("namecard", approvalService.searchNamecardDtoList(docsId).get(0));   // 저장된 데이터는 항상 1건이므로.

		return "approval/namecard/namecard-view";
	}
	@GetMapping(path = "/approval/namecard/modifypage/{docsId}")
	public String namecardModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		DocsDTO docsDTO = docsService.getDocsDto(docsId, DocumentType.APPROVAL, DocumentKind.NAMECARD);
		if(docsDTO == null)
			return Constants.ERROR_PAGE_410;
		if(!docsDTO.getMemberId().equals(loginUser.getMember().getId()))
			return Constants.ERROR_PAGE_403;
		
		if(approvalService.isProceedDocs(docsId)) {
			return "error/approval-already-proceeding";
		}

		model.addAttribute("docs", docsDTO).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "approval/namecard/namecard-modify";
	}
	// ----- ----- ----- ----- ----- namecard ----- ----- ----- ----- ----- //
	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- APPROVAL ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
	@GetMapping(path = "/schedule")
	public String scheduleFrame(Model model) {
		model.addAttribute("types", scheduleService.getTypes());
		return "full-calendar/fc-frame";
	}
	@GetMapping(path = "/schedule/main")
	public String scheduleMain() {
		return "full-calendar/fc-main";
	}
	@GetMapping(path = "/schedule/write/{type}")
	public String scheduleWrite(@PathVariable ScheduleType type, Model model) {
		model.addAttribute("type", type.name())   // front에서 window.opener로 typecode를 가져와야 하는데 제대로 못가져오는 경우가 간헐적으로 발생해서 추가.
			.addAttribute("codeList", basecodeService.getCodeList(BasecodeType.valueOf(type.name())));
		return "full-calendar/fc-write";
	}
	@GetMapping(path = "/schedule/details/{id}")
	public String scheduleDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal SecurityUser loginUser) {
		ScheduleDTO scheduleData = scheduleService.getScheduleDto(id);
		boolean updatable = scheduleData.getMemberId().equals(loginUser.getMember().getId());

		model.addAttribute("type", scheduleData.getType().name())   // front에서 window.opener로 typecode를 가져와야 하는데 제대로 못가져오는 경우가 간헐적으로 발생해서 추가.
			.addAttribute("codeList", basecodeService.getCodeList(BasecodeType.valueOf(scheduleData.getType().name())))
			.addAttribute("data", scheduleData)
			.addAttribute("updatable", updatable);
		return "full-calendar/fc-details";
	}
	// ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
}
