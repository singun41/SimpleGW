package com.project.simplegw.system.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosTempDayoff;
import com.project.simplegw.document.approval.vos.ApprovalRole;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsAddReferrer;
import com.project.simplegw.document.vos.EditorDocs;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.schedule.vos.SearchOption;
import com.project.simplegw.document.vos.DocsGroup;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.services.ViewService;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.Role;
import com.project.simplegw.upload.dtos.DtosAttachements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ViewController {
    private final ViewService service;   // 페이지에서 필요한 데이터들을 각각의 서비스클래스에서 바로 가져오지 않고 ViewService 클래스를 이용한다.
    private final MenuAuthorityService authority;

    @Autowired
    public ViewController(ViewService service, MenuAuthorityService authority) {
        this.service = service;
        this.authority = authority;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- main ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping(value = "/")
	public String indexRedirect() { return "redirect:main"; }

    @GetMapping(Constants.DEFAULT_MAIN_URL)
    public String defaultPage(HttpServletRequest req, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if(req.getHeader(Constants.USER_AGENT).toUpperCase().contains(Constants.MOBILE_CHECK_STR))
            return "redirect:m/main";   // url redirection : MobileViewController 클래스에서 URL 매핑

        model.addAttribute("user", service.getMyInfo(loginUser))   // user 기본 정보: MemberData 클래스

            // 메뉴 오픈 여부 전달
            .addAttribute("suggestion", authority.isAccessible(Menu.SUGGESTION, loginUser))
            .addAttribute("archive", authority.isAccessible(Menu.ARCHIVE, loginUser))

            .addAttribute("workRecord", authority.isAccessible(Menu.WORK_RECORD, loginUser))
            .addAttribute("workRecordTeam", authority.isAccessible(Menu.WORK_RECORD_TEAM, loginUser))
            .addAttribute("workRecordList", authority.isAccessible(Menu.WORK_RECORD_LIST, loginUser))
            .addAttribute("minutes", authority.isAccessible(Menu.MINUTES, loginUser))

            .addAttribute("approvalSearch", authority.isAccessible(Menu.APPROVAL_SEARCH, loginUser))
            .addAttribute("approvalDefault", authority.isAccessible(Menu.APPROVAL_DEFAULT, loginUser))
            .addAttribute("approvalCooperation", authority.isAccessible(Menu.APPROVAL_COOPERATION, loginUser))
            .addAttribute("approvalDayoff", authority.isAccessible(Menu.APPROVAL_DAYOFF, loginUser))

            .addAttribute("schedule", authority.isAccessible(Menu.SCHEDULE, loginUser))
            .addAttribute("employees", authority.isAccessible(Menu.EMPLOYEES, loginUser))
            ;
        return "main/main";
    }

    @GetMapping(path = {"/content", "/page/content"})
    public String contentPage(Model model) {
        model.addAttribute("employeeCnt", service.workingEmployeeCount())
            .addAttribute("scheduleSummary", service.getTodayScheduleCount())
            .addAttribute("scheduleMemberList", service.getTodayScheduleMemberList());
        return "main/content";
    }

    @GetMapping("/main/calendar")
    public String mainPageCalendarArea() {
        return "main/calendar/calendar";
    }

    @GetMapping("/main/calendar/new")
    public String mainPageCalendarScheduleAddPage(Model model) {
        model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage());
        return "main/calendar/new";
    }

    @GetMapping("/main/calendar/{id}")   // 아래 schedule/calendar/personal 메서드와 동일하게 처리.
    public String mainPageCalendarScheduleViewPage(@PathVariable Long id, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        Menu menu = Menu.SCHEDULE;

        DtosSchedule data = service.getSchedule(id);
        
        if( ! ( authority.isAccessible(menu, loginUser) && authority.isReadable(menu, loginUser, data.getMemberId()) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage());
        boolean isUpdatable = authority.isUpdatable(menu, loginUser, data.getMemberId()) && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 업데이트 가능.
        boolean isDeletable = authority.isDeletable(menu, loginUser, data.getMemberId()) && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 삭제 가능.

        model.addAttribute("pageTitle", ScheduleType.PERSONAL.getTitle()).addAttribute("data", data)
            .addAttribute("isUpdatable", isUpdatable).addAttribute("isDeletable", isDeletable);
        return "main/calendar/view";
    }

    @GetMapping("/page/alarm")
    public String alarmListPage() {
        return "main/alarm/list";
    }

    @GetMapping("/page/alarm/new")
    public String alarmNewPage() {
        return "main/alarm/new";
    }

    @GetMapping("/page/alarm/{id}")
    public String alarmEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("alarm", service.getAlarm(id));
        return "main/alarm/edit";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- main ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // SecurityConfig에서 접근 권한을 직접 지정하므로 MenuAuthorityService를 사용하지 않음.

    @GetMapping("/page/admin/users")
    public String usersPage() {
        return "admin/users/list";
    }

    @GetMapping("/page/admin/user/new")
    public String userNewPage(Model model) {
        model.addAttribute("jobTitles", service.getJobTitles());
        return "admin/users/new";
    }

    @GetMapping("/page/admin/user/profiles/{id}")
    public String userProfilePage(@PathVariable Long id, Model model) {
        model.addAttribute("roles", Role.values())
            .addAttribute("jobTitles", service.getJobTitles())
            .addAttribute("user", service.getMemberDetails(id));
        return "admin/users/profiles";
    }

    @GetMapping("/page/admin/user/pw/{id}")
    public String userPwPage(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.getMember(id));
        return "admin/users/pw-force";
    }

    @GetMapping("/page/admin/user/add-on/{id}")
    public String userAddOnDataPage(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.getMemberAddOn(id));
        return "admin/users/add-on";
    }




    @GetMapping("/page/admin/auths")
    public String authsPage(Model model) {
        // 수신 문서는 권한여부가 필요하지 않으므로 제외한다
        model.addAttribute("menus", Arrays.stream(Menu.values()).filter(e -> e != Menu.APPROVAL_RECEIVED).iterator());
        return "admin/auths/list";
    }

    @GetMapping("/page/admin/auths/edit/{id}")
    public String authEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("auth", authority.get(id));
        return "admin/auths/edit";
    }




    @GetMapping("/page/admin/codes")
    public String codesPage(Model model) {
        model.addAttribute("types", service.getBasecodeTypes());
        return "admin/codes/list";
    }

    @GetMapping("/page/admin/code/{id}")
    public String codeEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("code", service.getCode(id));
        return "admin/codes/edit";
    }

    @GetMapping("/page/admin/code/new/{type}")
    public String codeNewPage(@PathVariable String type, Model model) {
        model.addAttribute("type", BasecodeType.valueOf(type.toUpperCase()));
        return "admin/codes/new";
    }



    @GetMapping("/page/admin/forms")
    public String formsPage(Model model) {
        model.addAttribute("forms", EditorDocs.values());
        return "admin/forms/setting";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private List<DtosAttachements> getAttachmentsList(Long docsId) {
        return service.getAttachmentsList(docsId);
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/notice/list")
    public String noticeListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.NOTICE, loginUser) )
            return Constants.ERROR_PAGE_403;

        boolean isWritable = authority.isWritable(Menu.NOTICE, loginUser);

        model.addAttribute("docsType", DocsType.NOTICE)
            .addAttribute("isWritable", isWritable);
        return "docs/board/notice/list";
    }


    @GetMapping("/page/notice/write")
    public String noticeWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isWritable(Menu.NOTICE, loginUser) )  )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.NOTICE)
            .addAttribute("form", service.getDocsForm(EditorDocs.NOTICE));
        return "docs/board/notice/write";
    }


    @GetMapping("/page/notice/{docsId}")
    public String noticeViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getNotice(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isReadable(Menu.NOTICE, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.NOTICE, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.NOTICE, loginUser, docs.getWriterId());

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "docs/board/notice/view";
    }


    @GetMapping("/page/notice/{docsId}/modify")
    public String noticeModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isWritable(Menu.NOTICE, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getNotice(docsId);

        if( ! authority.isUpdatable(Menu.NOTICE, loginUser, docs.getWriterId()) )
            return Constants.ERROR_PAGE_403;

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/board/notice/modify";
    }



    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/notice/temp/{docsId}")
    public String noticeTempViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getTempNotice(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("docs", docs).addAttribute("isOwner", isOwner);
        return "docs/board/notice/temp/view";
    }


    @GetMapping("/page/notice/temp/{docsId}/modify")
    public String noticeTempModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.NOTICE, loginUser) && authority.isWritable(Menu.NOTICE, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getTempFreeboard(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docs", docs);
        return "docs/board/notice/temp/modify";
    }
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/freeboard/list")
    public String freeboardListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.FREEBOARD, loginUser) )
	        return Constants.ERROR_PAGE_403;

        boolean isWritable = authority.isWritable(Menu.FREEBOARD, loginUser);

        model.addAttribute("docsType", DocsType.FREEBOARD)
            .addAttribute("isWritable", isWritable);
        return "docs/board/free/list";
    }


    @GetMapping("/page/freeboard/write")
    public String freeboardWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isWritable(Menu.FREEBOARD, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.FREEBOARD)
            .addAttribute("form", service.getDocsForm(EditorDocs.FREEBOARD));
        return "docs/board/free/write";
    }


    @GetMapping("/page/freeboard/{docsId}")
    public String freeboardViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getFreeboard(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isReadable(Menu.FREEBOARD, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.FREEBOARD, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.FREEBOARD, loginUser, docs.getWriterId());

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "docs/board/free/view";
    }


    @GetMapping("/page/freeboard/{docsId}/modify")
    public String freeboardModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isWritable(Menu.FREEBOARD, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getFreeboard(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/board/free/modify";
    }



    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/freeboard/temp/{docsId}")
    public String freeboardTempViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getTempFreeboard(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("docs", docs).addAttribute("isOwner", isOwner);
        return "docs/board/free/temp/view";
    }


    @GetMapping("/page/freeboard/temp/{docsId}/modify")
    public String freeboardTempModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.FREEBOARD, loginUser) && authority.isWritable(Menu.FREEBOARD, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getTempFreeboard(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docs", docs);
        return "docs/board/free/temp/modify";
    }
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- suggestion ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/suggestion/list")
    public String suggestionListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.SUGGESTION, loginUser) )
	        return Constants.ERROR_PAGE_403;

        boolean isWritable = authority.isWritable(Menu.SUGGESTION, loginUser);

        model.addAttribute("docsType", DocsType.SUGGESTION)
            .addAttribute("isWritable", isWritable);
        return "docs/board/suggestion/list";
    }


    @GetMapping("/page/suggestion/write")
    public String suggestionWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isWritable(Menu.SUGGESTION, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.SUGGESTION)
            .addAttribute("form", service.getDocsForm(EditorDocs.SUGGESTION));
        return "docs/board/suggestion/write";
    }


    @GetMapping("/page/suggestion/{docsId}")
    public String suggestionViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getSuggestion(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isReadable(Menu.SUGGESTION, loginUser, docs.getWriterId()) ) )
	        return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.SUGGESTION, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.SUGGESTION, loginUser, docs.getWriterId());

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "docs/board/suggestion/view";
    }


    @GetMapping("/page/suggestion/{docsId}/modify")
    public String suggestionModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isWritable(Menu.SUGGESTION, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getSuggestion(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/board/suggestion/modify";
    }



    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/suggestion/temp/{docsId}")
    public String suggestionTempViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getTempSuggestion(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("docs", docs).addAttribute("isOwner", isOwner);
        return "docs/board/suggestion/temp/view";
    }


    @GetMapping("/page/suggestion/temp/{docsId}/modify")
    public String suggestionTempModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.SUGGESTION, loginUser) && authority.isWritable(Menu.SUGGESTION, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getTempSuggestion(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docs", docs);
        return "docs/board/suggestion/temp/modify";
    }
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- suggestion ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/archive/list")
    public String archiveListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.ARCHIVE, loginUser) )
	        return Constants.ERROR_PAGE_403;

        boolean isWritable = authority.isWritable(Menu.ARCHIVE, loginUser);

        model.addAttribute("docsType", DocsType.ARCHIVE)
            .addAttribute("isWritable", isWritable);
        return "docs/board/archive/list";
    }


    @GetMapping("/page/archive/write")
    public String archiveWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.ARCHIVE, loginUser) && authority.isWritable(Menu.ARCHIVE, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.ARCHIVE)
            .addAttribute("form", service.getDocsForm(EditorDocs.ARCHIVE));
        return "docs/board/archive/write";
    }


    @GetMapping("/page/archive/{docsId}")
    public String archiveViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getArchive(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.ARCHIVE, loginUser) && authority.isReadable(Menu.ARCHIVE, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.ARCHIVE, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.ARCHIVE, loginUser, docs.getWriterId());

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "docs/board/archive/view";
    }

    
    @GetMapping("/page/archive/{docsId}/modify")
    public String archiveModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.ARCHIVE, loginUser) && authority.isWritable(Menu.ARCHIVE, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getArchive(docsId);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/board/archive/modify";
    }

    
    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    // Archive(자료실)은 임시저장 문서를 제공하지 않는다.
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- ----- ----- ↑ //



    @GetMapping("/page/board/referrer-add")   // 문서 참조자 추가 페이지
    public String boardReferrerAddPage(Model model) {
        model.addAttribute("teams", service.getTeams());
        return "docs/board/common/view/referrer-add";
    }


    // ↓ ----- ----- ----- ----- ----- ----- ----- minutes ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/minutes/list")
    public String minutesListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.MINUTES, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        boolean isWritable = authority.isWritable(Menu.MINUTES, loginUser);

        model.addAttribute("docsType", DocsType.MINUTES)
            .addAttribute("isWritable", isWritable);
        return "docs/board/minutes/list";
    }


    @GetMapping("/page/minutes/write")
    public String minutesWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isWritable(Menu.MINUTES, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.MINUTES)
            .addAttribute("form", service.getDocsForm(EditorDocs.MINUTES));
        return "docs/board/minutes/write";
    }


    @GetMapping("/page/minutes/{docsId}")
    public String minutesViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocsAddReferrer docs = service.getMinutes(docsId, loginUser);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isReadable(Menu.MINUTES, loginUser, docs.getWriterId()) ) )
            return Constants.ERROR_PAGE_403;

        // 작성자가 아닌 경우 공유된 리스트에 없거나 관리자 권한이 아니면 접근 막기
        if( !docs.getWriterId().equals( loginUser.getMember().getId() ) && docs.getReferrers().stream().filter(e -> e.getMemberId().equals( loginUser.getMember().getId() )).findFirst().isEmpty() )
            if(loginUser.getMember().getRole() != Role.ADMIN)
                return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(Menu.MINUTES, loginUser, docs.getWriterId());
        boolean isDeletable = authority.isDeletable(Menu.MINUTES, loginUser, docs.getWriterId());

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable);
        return "docs/board/minutes/view";
    }


    @GetMapping("/page/minutes/{docsId}/modify")
    public String minutesModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.MINUTES, loginUser) && authority.isWritable(Menu.MINUTES, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        DtosDocs docs = service.getMinutes(docsId, loginUser);
        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/board/minutes/modify";
    }
    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    // Minutes(회의록)은 임시저장 문서를 제공하지 않는다.
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- minutes ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- common temp docs list ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/docs/temp/list")
    public String tempDocsListPage(Model model) {
        model.addAttribute("pageTitle", "임시저장 문서");
        return "docs/common/temp/list";
    }
    // ↑ ----- ----- ----- ----- ----- common temp docs list ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/work-record/personal")
    public String workRecordPersonalPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.WORK_RECORD, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("pageTitle", "업무일지");
        return "work-record/personal";
    }


    @GetMapping("/page/work-record/team")
    public String workRecordTeamPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.WORK_RECORD_TEAM, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("pageTitle", "부서 업무일지");
        return "work-record/team";
    }


    @GetMapping("/page/work-record/list")
    public String workRecordListPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! authority.isAccessible(Menu.WORK_RECORD_LIST, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("teams", service.getTeams()).addAttribute("pageTitle", "업무일지 리스트");
        return "work-record/list";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- work ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // ↓ ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ↓ //
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



    @GetMapping("/page/approval/proceed-list")
    public String proceedApprovalListPage(Model model) {
        model.addAttribute("pageTitle", "진행중인 결재문서");
        return "docs/approval/list/proceed";
    }


    @GetMapping("/page/approval/received-list/{roleStr}/new")
    public String receivedNewApprovalListPage(@PathVariable String roleStr, Model model) {
        ApprovalRole role = ApprovalRole.valueOf(roleStr.toUpperCase());
        model.addAttribute("pageTitle", role == ApprovalRole.APPROVER ? "결재 요청 문서" : "결재 참조 문서")
            .addAttribute("useRole", true).addAttribute("role", role)
            .addAttribute("useDate", false);
        return "docs/approval/list/received";
    }

    @GetMapping("/page/approval/received-list")
    public String receivedApprovalListSearchPage(Model model) {
        model.addAttribute("pageTitle", Menu.APPROVAL_RECEIVED.getTitle())
            .addAttribute("useRole", false).addAttribute("roles", ApprovalRole.values())
            .addAttribute("useDate", true)
            .addAttribute("types", Arrays.asList(DocsType.values()).stream().filter(e -> e.getGroup() == DocsGroup.APPROVAL).collect(Collectors.toList()));
        return "docs/approval/list/received";
    }


    @GetMapping("/page/approval/finished-list")
    public String finishedApprovalListPage(Model model) {
        model.addAttribute("pageTitle", "완결된 결재문서")
            .addAttribute("types", Arrays.asList(DocsType.values()).stream().filter(e -> e.getGroup() == DocsGroup.APPROVAL).collect(Collectors.toList()));
        return "docs/approval/list/finished";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- auth ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/approval/list-search")
    public String approvalDocsSearchPage(Model model) {
        model.addAttribute("pageTitle", Menu.APPROVAL_SEARCH.getTitle())
            .addAttribute("teams", service.getTeams())
            .addAttribute("types", Arrays.asList(DocsType.values()).stream().filter(e -> e.getGroup() == DocsGroup.APPROVAL).collect(Collectors.toList()));
        return "docs/approval/list/list-search";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- auth ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- line ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/approval/line-set")   // 결재선 지정 페이지
    public String lineSettingPage(Model model) {
        model.addAttribute("teams", service.getTeams());
        return "docs/approval/common/write/line-set";
    }

    @GetMapping("/page/approval/referrer-add")   // 문서 참조자 추가 페이지
    public String approvalReferrerAddPage(Model model) {
        model.addAttribute("teams", service.getTeams());
        return "docs/approval/common/view/referrer-add";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- line ----- ----- ----- ----- ----- ----- ----- ↑ //

    


    // 결재문서는 각 메뉴별 list 페이지를 만들지 않고, 하나의 리스트 페이지에서 모두 보여준다.

    
    // ↓ ----- ----- ----- ----- ----- ----- ----- default ----- ----- ----- ----- ----- ----- ----- ↓ //
    // 기본 양식의 결재문서는 DocsType을 문자열로 받은뒤 Enum으로 변환해 서비스로 전달한다.
    @GetMapping("/page/approval/forms/{type}/write")
    public String defaultApprovalWritePage(@PathVariable String type, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DocsType docsType = DocsType.valueOf(type.toUpperCase());
        Menu menu = docsType.getMenu();
        EditorDocs editorDocs = EditorDocs.valueOf(menu.name());

        if( ! ( authority.isAccessible(menu, loginUser) && authority.isWritable(menu, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", docsType)
            .addAttribute("form", service.getDocsForm(editorDocs));
        return "docs/approval/default/write";
    }

    @GetMapping("/page/approval/forms/{type}/{docsId}")
    public String defaultApprovalViewPage(@PathVariable String type, @PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DocsType docsType = DocsType.valueOf(type.toUpperCase());
        Menu menu = docsType.getMenu();

        DtosApprovalDocsCommon docs = service.getDefaultApproval(docsType, docsId, loginUser);

        if( ! approvalDocsReadable(docs, loginUser) )
            return Constants.ERROR_PAGE_403;
        
        boolean isOwner = docs.getWriterId().equals(loginUser.getMember().getId());
        boolean isProceed = isProceed(docs);
        boolean isUpdatable = isProceed ? false : authority.isUpdatable(menu, loginUser, docs.getWriterId());
        boolean isDeletable = isProceed ? false : authority.isDeletable(menu, loginUser, docs.getWriterId());
        boolean isCurrentApprover = isCurrentApprover(docs, loginUser);

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isOwner", isOwner)
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable)
            .addAttribute("isCurrentApprover", isCurrentApprover);
        return "docs/approval/default/view";
    }

    @GetMapping("/page/approval/forms/{type}/{docsId}/modify")
    public String defaultApprovalModifyPage(@PathVariable String type, @PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DocsType docsType = DocsType.valueOf(type.toUpperCase());
        Menu menu = docsType.getMenu();

        DtosApprovalDocsCommon docs = service.getDefaultApproval(docsType, docsId, loginUser);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if( ! ( authority.isAccessible(menu, loginUser) && authority.isUpdatable(menu, loginUser, docs.getWriterId()) && isOwner ) )
            return Constants.ERROR_PAGE_403;

        if(isProceed(docs))
            return Constants.ERROR_PAGE_403_MODIFY;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId));
        return "docs/approval/default/modify";
    }


    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/approval/forms/{type}/temp/{docsId}")
    public String defaultApprovalTempViewPage(@PathVariable String type, @PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DocsType docsType = DocsType.valueOf(type.toUpperCase());
        DtosDocs docs = service.getTempDefaultApproval(docsType, docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("docs", docs).addAttribute("isOwner", isOwner);
        return "docs/approval/default/temp/view";
    }


    @GetMapping("/page/approval/forms/{type}/temp/{docsId}/modify")
    public String defaultApprovalTempModifyPage(@PathVariable String type, @PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DocsType docsType = DocsType.valueOf(type.toUpperCase());
        DtosDocs docs = service.getTempDefaultApproval(docsType, docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docs", docs);
        return "docs/approval/default/temp/modify";
    }
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- default ----- ----- ----- ----- ----- ----- ----- ↑ //


    
    // 여기서부터 별도양식 결재문서 페이지

    // ↓ ----- ----- ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/approval/dayoff/write")
    public String dayoffApprovalWritePage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        if( ! ( authority.isAccessible(Menu.APPROVAL_DAYOFF, loginUser) && authority.isWritable(Menu.APPROVAL_DAYOFF, loginUser) ) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docsType", DocsType.DAYOFF)
            .addAttribute("codes", service.getDayoffCodes());

        return "docs/approval/dayoff/write";
    }

    @GetMapping("/page/approval/dayoff/{docsId}")
    public String dayoffApprovalViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDayoff docs = service.getDayoffApproval(docsId, loginUser);
        Menu menu = Menu.APPROVAL_DAYOFF;

        if( ! approvalDocsReadable(docs, loginUser) )
            return Constants.ERROR_PAGE_403;

        boolean isOwner = docs.getWriterId().equals(loginUser.getMember().getId());
        boolean isProceed = isProceed(docs);
        boolean isUpdatable = isProceed ? false : authority.isUpdatable(menu, loginUser, docs.getWriterId());
        boolean isDeletable = isProceed ? false : authority.isDeletable(menu, loginUser, docs.getWriterId());
        boolean isCurrentApprover = isCurrentApprover(docs, loginUser);

        model.addAttribute("docs", docs)
            .addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("isOwner", isOwner)
            .addAttribute("isUpdatable", isUpdatable)
            .addAttribute("isDeletable", isDeletable)
            .addAttribute("isCurrentApprover", isCurrentApprover);

        return "docs/approval/dayoff/view";
    }

    @GetMapping("/page/approval/dayoff/{docsId}/modify")
    public String dayoffApprovalModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        Menu menu = Menu.APPROVAL_DAYOFF;
        DtosApprovalDocsCommon docs = service.getDefaultApproval(DocsType.DAYOFF, docsId, loginUser);

        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if( ! ( authority.isAccessible(menu, loginUser) && authority.isUpdatable(menu, loginUser, docs.getWriterId()) && isOwner ) )
            return Constants.ERROR_PAGE_403;

        if(isProceed(docs))
            return Constants.ERROR_PAGE_403_MODIFY;

        model.addAttribute("docs", docs).addAttribute("attachmentsList", getAttachmentsList(docsId))
            .addAttribute("codes", service.getDayoffCodes());
        return "docs/approval/dayoff/modify";
    }


    // ↓ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/approval/dayoff/temp/{docsId}")
    public String dayoffApprovalTempViewPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosTempDayoff docs = service.getTempDayoffApproval(docsId);
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;
        
        model.addAttribute("docs", docs).addAttribute("isOwner", isOwner);
        return "docs/approval/dayoff/temp/view";
    }

    @GetMapping("/page/approval/dayoff/temp/{docsId}/modify")
    public String dayoffApprovalTempModifyPage(@PathVariable Long docsId, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        DtosDocs docs = service.getTempDefaultApproval(DocsType.DAYOFF, docsId);   // 수정페이지 진입시에는 디테일은 페이지 로드 후 ajax로 별도 호출.
        boolean isOwner = loginUser.getMember().getId().equals( docs.getWriterId() );

        if(docs.getId() == null)
            return Constants.ERROR_PAGE_410;
        if( ! isOwner )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("docs", docs).addAttribute("codes", service.getDayoffCodes());
        return "docs/approval/dayoff/temp/modify";
    }
    // ↑ ----- ----- ----- ----- ----- temp ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- dayoff ----- ----- ----- ----- ----- ----- ----- ↑ //
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- employees ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/employees")
    public String employeesPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        Menu menu = Menu.EMPLOYEES;
        if( ! authority.isAccessible(menu, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("teams", service.getTeams()).addAttribute("pageTitle", menu.getTitle());
        return "employees/list";
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- employees ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/page/schedule")
    public String scheduleContainerPage(Model model, @AuthenticationPrincipal LoginUser loginUser) {
        Menu menu = Menu.SCHEDULE;
        if( ! authority.isAccessible(menu, loginUser) )
            return Constants.ERROR_PAGE_403;

        model.addAttribute("pageTitle", menu.getTitle())
            .addAttribute("types", ScheduleType.values())
            .addAttribute("options", SearchOption.values());
        return "schedule/container";
    }

    @GetMapping("/schedule/calendar")
    public String scheduleCalendarPage(@AuthenticationPrincipal LoginUser loginUser) {
        Menu menu = Menu.SCHEDULE;
        if( ! authority.isAccessible(menu, loginUser) )
            return Constants.ERROR_PAGE_403;

        return "schedule/calendar";
    }

    @GetMapping("/page/schedule/{type}/new")
    public String scheduleWritePage(@PathVariable("type") String typeStr, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        ScheduleType type = ScheduleType.valueOf(typeStr.toUpperCase());

        Menu menu = Menu.SCHEDULE;
        if( ! (authority.isAccessible(menu, loginUser) && authority.isWritable(menu, loginUser)) )
            return Constants.ERROR_PAGE_403;
        
        
        switch(type) {
            case PERSONAL -> {
                model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage());
            }
            default -> {}
        }
        
        model.addAttribute("pageTitle", type.getTitle());
        return new StringBuilder("schedule/").append(type.name().toLowerCase()).append("/new").toString();
    }

    @GetMapping("/page/schedule/{type}/{id}")
    public String scheduleViewPage(@PathVariable("type") String typeStr, @PathVariable Long id, Model model, @AuthenticationPrincipal LoginUser loginUser) {
        ScheduleType type = ScheduleType.valueOf(typeStr.toUpperCase());
        Menu menu = Menu.SCHEDULE;

        DtosSchedule data = service.getSchedule(id);
        
        if( ! ( authority.isAccessible(menu, loginUser) && authority.isReadable(menu, loginUser, data.getMemberId()) ) )
            return Constants.ERROR_PAGE_403;

        boolean isUpdatable = authority.isUpdatable(menu, loginUser, data.getMemberId());
        boolean isDeletable = authority.isDeletable(menu, loginUser, data.getMemberId());

        switch(type) {
            case PERSONAL -> {
                model.addAttribute("codes", service.getPersonalScheduleCodeToNewPage());
                isUpdatable = isUpdatable && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 업데이트 가능.
                isDeletable = isDeletable && (data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || data.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) && ! data.getDateFrom().isBefore(LocalDate.now());   // 외근, 직출직퇴만, 오늘 일정이거나 이후 일정만 삭제 가능.
            }
            default -> {}
        }

        model.addAttribute("pageTitle", type.getTitle()).addAttribute("data", data)
            .addAttribute("isUpdatable", isUpdatable).addAttribute("isDeletable", isDeletable);
        return new StringBuilder("schedule/").append(type.name().toLowerCase()).append("/view").toString();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
