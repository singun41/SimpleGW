package com.project.simplegw.mobile.common.services;

import com.project.simplegw.common.services.BasecodeService;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.services.AttachmentsService;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.mobile.document.services.MobileDocsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path = "/mobile", method = RequestMethod.GET)
public class MobileViewController {
    
    private final MobileDocsService docsService;
    private final AttachmentsService attachmentsService;
    private final BasecodeService basecodeService;
    private final MemberService memberService;
    
    @Autowired
    public MobileViewController(MobileDocsService docsService, AttachmentsService attachmentsService, BasecodeService basecodeService, MemberService memberService) {
        this.docsService = docsService;
        this.attachmentsService = attachmentsService;
        this.basecodeService = basecodeService;
        this.memberService = memberService;
    }

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- BOARD ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
	// ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //
    @GetMapping("/notice/listpage")
    public String noticeListPage() {
        return "mobile/board/notice/notice-list";
    }

    @GetMapping(path = "/notice/view/{docsId}")
    public String noticeView(@PathVariable(name = "docsId") Long docsId, Model model) {
        DocsDTO docsDto = docsService.getDocsDto(docsId);
        if(docsDto == null) {
			return Constants.ERROR_PAGE_410;
		}

		model.addAttribute("docs", docsDto).addAttribute("fileList", attachmentsService.getFileList(docsId));
		return "mobile/board/notice/notice-view";
    }
    // ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- BOARD ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- APPROVAL ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- //
    @GetMapping("/approval/common/approval/line")
    public String approvalLinePage(Model model) {
		model.addAttribute("teamList", memberService.getTeamList());
		return "mobile/approval/common/approval-line";
	}
    // ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- //

    // ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //
    @GetMapping("/approval/dayoff/listpage")
    public String dayoffListPage() {
        return "mobile/approval/dayoff/dayoff-list";
    }

    @GetMapping("/approval/dayoff/writepage")
    public String dayoffWritePage(Model model) {
        model.addAttribute("dayoffList", basecodeService.getCodeList(BasecodeType.DAYOFF));
        return "mobile/approval/dayoff/dayoff-write";
    }
    // ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- APPROVAL ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
}
