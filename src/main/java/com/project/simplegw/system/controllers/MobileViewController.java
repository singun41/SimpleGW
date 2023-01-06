package com.project.simplegw.system.controllers;

import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.schedule.vos.SearchOption;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.services.MobileViewService;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.Role;
import com.project.simplegw.upload.dtos.DtosAttachements;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MobileViewController {
    private final MobileViewService service;
    private final MenuAuthorityService authority;

    @Autowired
    public MobileViewController(MobileViewService service, MenuAuthorityService authority) {
        this.service = service;
        this.authority = authority;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    @GetMapping("/mobile")
    public String redirect() { return "redirect:m/main"; }

    @GetMapping(path = {Constants.MOBILE_MAIN_URL, "/page/m"})
    public String mainPage(Model model) {
        model.addAttribute("pageTitle", "Main");
        return "mobile/main";
    }




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/admin/management")
    public String managementsPage(Model model) {
        model.addAttribute("pageTitle", "Management");
        return "mobile/admin/menu";
    }




    @GetMapping("/page/m/admin/users")
    public String usersPage(Model model) {
        model.addAttribute("pageTitle", "Users");
        return "mobile/admin/users/list";
    }

    @GetMapping("/page/m/admin/user/new")
    public String usersCreationPage(Model model) {
        model.addAttribute("pageTitle", "User creation").addAttribute("jobTitles", service.getJobTitles());
        return "mobile/admin/users/new";
    }

    @GetMapping("/page/m/admin/user/profiles/{id}")
    public String usersProfilePage(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "User profiles edit")
            .addAttribute("roles", Role.values())
            .addAttribute("jobTitles", service.getJobTitles())
            .addAttribute("user", service.getMemberDetails(id));
        return "mobile/admin/users/profiles";
    }

    @GetMapping("/page/m/admin/user/pw/{id}")
    public String userPwPage(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "User password update")
            .addAttribute("user", service.getMember(id));
        return "mobile/admin/users/pw-force";
    }




    @GetMapping("/page/m/admin/auths")
    public String authsPage(Model model) {
        model.addAttribute("pageTitle", "Auths")
            .addAttribute("menus", Arrays.stream(Menu.values()).filter(e -> e != Menu.APPROVAL_RECEIVED).iterator());
        return "mobile/admin/auths/list";
    }

    @GetMapping("/page/m/admin/auths/edit/{id}")
    public String authEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Authority edit").addAttribute("auth", authority.get(id));
        return "mobile/admin/auths/edit";
    }




    @GetMapping("/page/m/admin/codes")
    public String codessPage(Model model) {
        model.addAttribute("pageTitle", "Codes").addAttribute("types", service.getBasecodeTypes());
        return "mobile/admin/codes/list";
    }

    @GetMapping("/page/m/admin/code/{id}")
    public String codeEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Code edit").addAttribute("code", service.getCode(id));
        return "mobile/admin/codes/edit";
    }

    @GetMapping("/page/m/admin/code/new/{type}")
    public String codeNewPage(@PathVariable String type, Model model) {
        model.addAttribute("pageTitle", "Code creation")
            .addAttribute("type", BasecodeType.valueOf(type.toUpperCase()));
        return "mobile/admin/codes/new";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //






    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/board")
    public String boardPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", "게시판");
        return "mobile/docs/board/menu";
    }


    private List<DtosAttachements> getAttachmentsList(Long docsId) {
        return service.getAttachmentsList(docsId);
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/notice/list")
    public String noticeMainListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.NOTICE.getTitle()).addAttribute("docsType", DocsType.NOTICE);
        return "mobile/docs/board/list-main";
    }

    @GetMapping("/page/m/notice/search")
    public String noticeSearchListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.NOTICE.getTitle()).addAttribute("pageType", Menu.NOTICE);
        return "mobile/docs/board/list-search";
    }

    @GetMapping("/page/m/notice/{docsId}")
    public String noticeViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getNotice(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isReadable(Menu.NOTICE, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.NOTICE, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.NOTICE, loginUser, docs.getWriterId());

        model.addAttribute("pageTitle", Menu.NOTICE.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "mobile/docs/board/view";
    }

    @GetMapping("/page/m/notice/write")
    public String noticeWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isWritable(Menu.NOTICE, loginUser) )  )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", Menu.NOTICE.getTitle()).addAttribute("docsType", DocsType.NOTICE);
        return "mobile/docs/board/write";
    }

    @GetMapping("/page/m/notice/{docsId}/modify")
    public String noticeModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isWritable(Menu.NOTICE, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getNotice(docsId);

        if( ! authority.isUpdatable(Menu.NOTICE, loginUser, docs.getWriterId()) )
            return Constants.ERROR_PAGE_403;

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("pageTitle", Menu.NOTICE.getTitle())
            .addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "mobile/docs/board/modify";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/freeboard/list")
    public String freeboardMainListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.FREEBOARD.getTitle()).addAttribute("docsType", DocsType.FREEBOARD);
        return "mobile/docs/board/list-main";
    }

    @GetMapping("/page/m/freeboard/search")
    public String freeboardSearchListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.FREEBOARD.getTitle()).addAttribute("pageType", Menu.FREEBOARD);
        return "mobile/docs/board/list-search";
    }

    @GetMapping("/page/m/freeboard/{docsId}")
    public String freeboardViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getFreeboard(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isReadable(Menu.FREEBOARD, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.FREEBOARD, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.FREEBOARD, loginUser, docs.getWriterId());

        model.addAttribute("pageTitle", Menu.FREEBOARD.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "mobile/docs/board/view";
    }

    @GetMapping("/page/m/freeboard/write")
    public String freeboardWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isWritable(Menu.FREEBOARD, loginUser) )  )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", Menu.FREEBOARD.getTitle()).addAttribute("docsType", DocsType.FREEBOARD);
        return "mobile/docs/board/write";
    }

    @GetMapping("/page/m/freeboard/{docsId}/modify")
    public String freeboardModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isWritable(Menu.FREEBOARD, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getFreeboard(docsId);

        if( ! authority.isUpdatable(Menu.FREEBOARD, loginUser, docs.getWriterId()) )
            return Constants.ERROR_PAGE_403;

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("pageTitle", Menu.FREEBOARD.getTitle())
            .addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "mobile/docs/board/modify";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- suggestion ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/suggestion/search")
    public String suggestionSearchListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.SUGGESTION.getTitle()).addAttribute("pageType", Menu.SUGGESTION);
        return "mobile/docs/board/list-search";
    }

    @GetMapping("/page/m/suggestion/{docsId}")
    public String suggestionViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getSuggestion(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isReadable(Menu.SUGGESTION, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.SUGGESTION, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.SUGGESTION, loginUser, docs.getWriterId());

        model.addAttribute("pageTitle", Menu.SUGGESTION.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "mobile/docs/board/view";
    }

    @GetMapping("/page/m/suggestion/write")
    public String suggestionWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isWritable(Menu.SUGGESTION, loginUser) )  )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", Menu.SUGGESTION.getTitle()).addAttribute("docsType", DocsType.SUGGESTION);
        return "mobile/docs/board/write";
    }

    @GetMapping("/page/m/suggestion/{docsId}/modify")
    public String suggestionModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isWritable(Menu.SUGGESTION, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getFreeboard(docsId);

        if( ! authority.isUpdatable(Menu.SUGGESTION, loginUser, docs.getWriterId()) )
            return Constants.ERROR_PAGE_403;

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("pageTitle", Menu.SUGGESTION.getTitle())
            .addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "mobile/docs/board/modify";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- suggestion ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/archive/search")
    public String archiveSearchListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.ARCHIVE.getTitle()).addAttribute("pageType", Menu.ARCHIVE);
        return "mobile/docs/board/list-search";
    }

    @GetMapping("/page/m/archive/{docsId}")
    public String archiveViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getArchive(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.ARCHIVE, loginUser) && authority.isReadable(Menu.ARCHIVE, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("pageTitle", Menu.ARCHIVE.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "mobile/docs/board/view";
    }

    // 자료실은 모바일 페이지에서의 작성, 수정을 제공하지 않는다.
    // ↑ ----- ----- ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- minutes ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/minutes/search")
    public String minutesSearchListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", Menu.MINUTES.getTitle()).addAttribute("pageType", Menu.MINUTES);
        return "mobile/docs/board/list-search";
    }

    @GetMapping("/page/m/minutes/{docsId}")
    public String minutesViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getMinutes(docsId, loginUser);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isReadable(Menu.MINUTES, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.MINUTES, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.MINUTES, loginUser, docs.getWriterId());

        model.addAttribute("pageTitle", Menu.MINUTES.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "mobile/docs/board/view";
    }

    @GetMapping("/page/m/minutes/write")
    public String minutesWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isWritable(Menu.MINUTES, loginUser) )  )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", Menu.MINUTES.getTitle()).addAttribute("docsType", DocsType.MINUTES);
        return "mobile/docs/board/write";
    }

    @GetMapping("/page/m/minutes/{docsId}/modify")
    public String minutesModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isWritable(Menu.MINUTES, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getFreeboard(docsId);

        if( ! authority.isUpdatable(Menu.MINUTES, loginUser, docs.getWriterId()) )
            return Constants.ERROR_PAGE_403;

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("pageTitle", Menu.MINUTES.getTitle())
            .addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "mobile/docs/board/modify";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- minutes ----- ----- ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/work-record")
    public String workRecordPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", "업무");
        return "mobile/work-record/menu";
    }

    @GetMapping("/page/m/work-record/personal")
    public String workRecordPersonalPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.WORK_RECORD, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", "업무 일지");
        return "mobile/work-record/personal";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // ↓ ----- ----- ----- ----- ----- ----- ----- list - proceed ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/approval/proceed-list")
    public String proceedApprovalListPage(Model model) {
        model.addAttribute("pageTitle", "진행중인 결재");
        return "mobile/docs/approval/list/proceed";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- list - proceed ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- list - finished ----- ----- ----- ----- ----- ----- ----- ↓ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- list - finished ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- list - received - approver ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/approval/new/received-approver")
    public String receivedNewApprovalListForApproverPage(Model model) {
        model.addAttribute("pageTitle", "결재 요청 문서");
        return "mobile/docs/approval/list/received-approver";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- list - received - approver ----- ----- ----- ----- ----- ----- ----- ↑ //



    // ↓ ----- ----- ----- ----- ----- ----- ----- list - received - referrer ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/approval/new/received-referrer")
    public String receivedNewApprovalListForReferrerPage(Model model) {
        model.addAttribute("pageTitle", "결재 참조 문서");
        return "mobile/docs/approval/list/received-referrer";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- list - received - referrer ----- ----- ----- ----- ----- ----- ----- ↑ //





    private boolean approvalDocsReadable(DtosApprovalDocsCommon docs, LoginUser loginUser) {   // 문서 작성자 or 결재 or 참조로 포함된 경우 볼 수 있음.
        Long userId = loginUser.getMember().getId();

        return docs.getWriterId().equals(userId) ||
            docs.getLine().getApprovers().stream().filter(e -> e.getMemberId().equals(userId)).findFirst().isPresent() ||
            isReferrer(docs, loginUser) ||
            authority.isAccessible(Menu.APPROVAL_SEARCH, loginUser);   // 결재문서 검색 메뉴 권한이 있으면 다른 결재문서를 볼 수 있다.
    }
    
    private boolean isProceed(DtosApprovalDocsCommon docs) {
        return docs.getLine().getApprovers().stream().filter(e -> e.getSign() == Sign.CONFIRMED || e.getSign() == Sign.REJECTED).findFirst().isPresent();
    }

    private boolean isCurrentApprover(DtosApprovalDocsCommon docs, LoginUser loginUser) {
        Optional<DtosApprover> target = docs.getLine().getApprovers().stream().filter(e -> e.getSign() == Sign.PROCEED).findFirst();
        if(target.isPresent())
            return target.get().getMemberId().equals( loginUser.getMember().getId() );
        
        return false;
    }

    private boolean isReferrer(DtosApprovalDocsCommon docs, LoginUser loginUser) {
        return docs.getLine().getReferrers().stream().filter(e -> e.getMemberId().equals( loginUser.getMember().getId() )).findFirst().isPresent();
    }


    
    @GetMapping("/page/m/approval")
    public String approvalPage(Model model) {
        model.addAttribute("pageTitle", "결재");
        return "mobile/docs/approval/menu";
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/approval/dayoff/write")
    public String dayoffApprovalWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.APPROVAL_DAYOFF, loginUser) && authority.isWritable(Menu.APPROVAL_DAYOFF, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("pageTitle", DocsType.DAYOFF.getTitle())
            .addAttribute("docsType", DocsType.DAYOFF)
            .addAttribute("teams", service.getTeams())
            .addAttribute("codes", service.getDayoffCodes());

        return "mobile/docs/approval/dayoff/write";
    }

    @GetMapping("/page/m/approval/dayoff/{docsId}")
    public String dayoffApprovalViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDayoff docs = service.getDayoffApproval(docsId, loginUser);

        if( ! approvalDocsReadable(docs, loginUser) )
            return Constants.ERROR_PAGE_403;

        boolean isOwner = docs.getWriterId().equals(loginUser.getMember().getId());
        boolean isProceed = isProceed(docs);
        boolean isUpdatable = isProceed ? false : authority.isUpdatable(Menu.APPROVAL_DAYOFF, loginUser, docs.getWriterId());
        boolean isDeletable = isProceed ? false : authority.isDeletable(Menu.APPROVAL_DAYOFF, loginUser, docs.getWriterId());
        boolean isCurrentApprover = isCurrentApprover(docs, loginUser);

        model.addAttribute("pageTitle", DocsType.DAYOFF.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isOwner", isOwner)
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable)
            .addAttribute("isCurrentApprover", isCurrentApprover);

        return "mobile/docs/approval/dayoff/view";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- default ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/approval/default/{docsId}")
    public String defaultApprovalViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosApprovalDocsCommon docs = service.getDefaultApproval(DocsType.DEFAULT, docsId, loginUser);

        if( ! approvalDocsReadable(docs, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        boolean isOwner = docs.getWriterId().equals(loginUser.getMember().getId());
        boolean isProceed = isProceed(docs);
        boolean isUpdatable = isProceed ? false : authority.isUpdatable(Menu.APPROVAL_DEFAULT, loginUser, docs.getWriterId());
        boolean isDeletable = isProceed ? false : authority.isDeletable(Menu.APPROVAL_DEFAULT, loginUser, docs.getWriterId());
        boolean isCurrentApprover = isCurrentApprover(docs, loginUser);

        model.addAttribute("pageTitle", DocsType.DEFAULT.getTitle())
            .addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isOwner", isOwner)
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable)
            .addAttribute("isCurrentApprover", isCurrentApprover);
        return "mobile/docs/approval/default/view";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- default ----- ----- ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- employees ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/employees")
    public String employeesListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.EMPLOYEES, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("teams", service.getTeams()).addAttribute("pageTitle", Menu.EMPLOYEES.getTitle());
        return "mobile/employees/list";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- employees ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- account ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/account")
    public String accountPage(Model model) {
        model.addAttribute("pageTitle", "내 계정");
        return "mobile/account/menu";
    }

    @GetMapping("/page/m/account/profile")
    public String accountProfilePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        model.addAttribute("pageTitle", "프로필")
            .addAttribute("profile", service.getProfile(loginUser));
        return "mobile/account/profile";
    }

    @GetMapping("/page/m/account/password")
    public String accountPasswordPage(Model model) {
        model.addAttribute("pageTitle", "패스워드 변경");
        return "mobile/account/password";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- account ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/m/schedule")
    public String schedulePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.SCHEDULE, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", "일정");
        return "mobile/schedule/menu";
    }

    @GetMapping("/page/m/schedule/list")
    public String scheduleListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.SCHEDULE, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("pageTitle", "일정 리스트")
            .addAttribute("types", ScheduleType.values())
            .addAttribute("options", SearchOption.values());
        return "mobile/schedule/list";
    }

    @GetMapping("/mobile/calendar")
    public String scheduleCalendarPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.SCHEDULE, loginUser) )
            return Constants.ERROR_PAGE_403;
        return "mobile/schedule/calendar";
    }

    @GetMapping("/page/m/schedule/personal/new")
    public String scheduleWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! (authority.isAccessible(Menu.SCHEDULE, loginUser) && authority.isWritable(Menu.SCHEDULE, loginUser)) )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage())
            .addAttribute("pageTitle", "일정 등록");
        return "mobile/schedule/personal/new";
    }

    @GetMapping("/page/m/schedule/personal/{id}")
    public String personalScheduleEditPage(@PathVariable Long id, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosSchedule data = service.getSchedule(id);
        
        if( ! ( authority.isAccessible(Menu.SCHEDULE, loginUser) && authority.isReadable(Menu.SCHEDULE, loginUser, data.getMemberId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.SCHEDULE, loginUser, data.getMemberId());
        boolean isDeletable = authority.isDeletable(Menu.SCHEDULE, loginUser, data.getMemberId());

        model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage());
        isUpdatable = isUpdatable && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 업데이트 가능.
        isDeletable = isDeletable && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 삭제 가능.

        model.addAttribute("pageTitle", "내 일정").addAttribute("data", data)
            .addAttribute("isUpdatable", isUpdatable).addAttribute("isDeletable", isDeletable);
        return "mobile/schedule/personal/edit";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
