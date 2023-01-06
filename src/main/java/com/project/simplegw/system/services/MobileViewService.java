package com.project.simplegw.system.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.dtos.send.DtosCodeValue;
import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.services.DefaultReportService;
import com.project.simplegw.document.approval.services.details.DayoffService;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsAddReferrer;
import com.project.simplegw.document.services.ArchiveService;
import com.project.simplegw.document.services.FreeboardService;
import com.project.simplegw.document.services.MinutesService;
import com.project.simplegw.document.services.NoticeService;
import com.project.simplegw.document.services.SuggestionService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.member.dtos.admin.send.DtosMemberDetails;
import com.project.simplegw.member.dtos.send.DtosProfile;
import com.project.simplegw.member.services.MemberAdminService;
import com.project.simplegw.member.services.MemberClientService;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.services.ScheduleService;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.upload.dtos.DtosAttachements;
import com.project.simplegw.upload.services.AttachmentsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MobileViewService {
    private final MemberService memberService;
    private final MemberClientService memberClientService;
    private final MemberAdminService memberAdminService;

    private final AttachmentsService attachmentsService;
    private final NoticeService noticeService;
    private final FreeboardService freeboardService;
    private final SuggestionService suggestionService;
    private final ArchiveService archiveService;
    private final MinutesService minutesService;

    private final BasecodeService basecodeService;

    private final ScheduleService scheduleService;

    private final DefaultReportService defaultReportService;
    private final DayoffService dayoffService;
    
    @Autowired
    public MobileViewService(
        MemberService memberService, MemberClientService memberClientService, MemberAdminService memberAdminService,
        AttachmentsService attachmentsService,
        NoticeService noticeService, FreeboardService freeboardService, SuggestionService suggestionService, ArchiveService archiveService, MinutesService minutesService,

        BasecodeService basecodeService, ScheduleService scheduleService,

        DefaultReportService defaultReportService, DayoffService dayoffService
    ) {
        this.memberService = memberService;
        this.memberClientService = memberClientService;
        this.memberAdminService = memberAdminService;
        
        this.attachmentsService = attachmentsService;
        this.noticeService = noticeService;
        this.freeboardService = freeboardService;
        this.suggestionService = suggestionService;
        this.archiveService = archiveService;
        this.minutesService = minutesService;

        this.basecodeService = basecodeService;

        this.scheduleService = scheduleService;

        this.defaultReportService = defaultReportService;
        this.dayoffService = dayoffService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    
    public MemberData getMyInfo(LoginUser loginUser) {
        return memberService.getMemberData(loginUser);
    }

    public DtosMemberDetails getMemberDetails(Long memberId) {
        return memberAdminService.getMemberDetails(memberId);
    }

    public DtosMember getMember(Long memberId) {
        return memberAdminService.getMember(memberId);
    }

    public DtosProfile getProfile(LoginUser loginUser) {
        return memberClientService.getProfile(loginUser).calcDuration();   // MemberService 클래스에서 캐싱하므로 여기서 계산해서 리턴.
    }




    public List<BasecodeType> getBasecodeTypes() {
        return basecodeService.getAllTypes();
    }

    public DtosBasecode getCode(Long id) {
        return basecodeService.getCode(id);
    }
    
    public List<String> getJobTitles() {
        return basecodeService.getJobTitles();
    }

    public List<String> getTeams() {
        return memberService.getTeams();
    }

    public List<DtosCodeValue> getDayoffCodes() {
        return basecodeService.getDayoffCodes();
    }
    

    public List<DtosCodeValue> getPersonalScheduleCodeToNewPage() {
        return basecodeService.getPersonalScheduleCodes().stream().filter(e -> {
            // 근태 결재문서 최종 승인시 자동 등록되는 코드는 제외하고 전달.
            return
                ! e.getKey().equals(ScheduleFixedPersonalCode.DAYOFF.getCode()) &&   // 휴가
                ! e.getKey().equals(ScheduleFixedPersonalCode.HALF_AM.getCode()) &&   // 반차(오전)
                ! e.getKey().equals(ScheduleFixedPersonalCode.HALF_PM.getCode());     // 반차(오후)
        }).collect(Collectors.toList());
    }




    public List<DtosAttachements> getAttachmentsList(Long docsId) {
        return attachmentsService.getAttachmentsList(docsId);
    }

    public DtosDocs getNotice(Long docsId) {
        return noticeService.getDocs(docsId);
    }

    public DtosDocs getFreeboard(Long docsId) {
        return freeboardService.getDocs(docsId);
    }

    public DtosDocs getSuggestion(Long docsId) {
        return suggestionService.getDocs(docsId);
    }

    public DtosDocs getArchive(Long docsId) {
        return archiveService.getDocs(docsId);
    }

    public DtosDocsAddReferrer getMinutes(Long docsId, LoginUser loginUser) {
        return minutesService.getDocs(docsId, loginUser);
    }




    public DtosSchedule getSchedule(Long id) {
        return scheduleService.getSchedule(id);
    }



    
    public DtosApprovalDocsCommon getDefaultApproval(DocsType docsType, Long docsId, LoginUser loginUser) {
        // 기본 결재문서 형식 외에 디테일이 있는 결재문서에서도 공통으로 사용함.
        // 디테일은 별도 api로 호출하고, 결재문서 기본 데이터만 가져오기 위함.
        return defaultReportService.getDocs(docsType, docsId, loginUser);
    }

    public DtosDayoff getDayoffApproval(Long docsId, LoginUser loginUser) {
        return dayoffService.getDocs(docsId, loginUser);
    }
}
