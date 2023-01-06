package com.project.simplegw.system.services;

import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.alarm.dtos.send.DtosAlarm;
import com.project.simplegw.alarm.services.AlarmService;
import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.dtos.send.DtosCodeValue;
import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.document.approval.dtos.send.DtosApprovalDocsCommon;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosDayoff;
import com.project.simplegw.document.approval.dtos.send.details.dayoff.DtosTempDayoff;
import com.project.simplegw.document.approval.services.DefaultReportService;
import com.project.simplegw.document.approval.services.details.DayoffService;
import com.project.simplegw.document.dtos.send.DtosDocs;
import com.project.simplegw.document.dtos.send.DtosDocsAddReferrer;
import com.project.simplegw.document.services.ArchiveService;
import com.project.simplegw.document.services.DocsFormService;
import com.project.simplegw.document.services.FreeboardService;
import com.project.simplegw.document.services.MinutesService;
import com.project.simplegw.document.services.NoticeService;
import com.project.simplegw.document.services.SuggestionService;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.document.vos.EditorDocs;
import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.member.dtos.admin.send.DtosMemberAddOn;
import com.project.simplegw.member.dtos.admin.send.DtosMemberDetails;
import com.project.simplegw.member.dtos.send.DtosProfile;
import com.project.simplegw.member.services.MemberAdminService;
import com.project.simplegw.member.services.MemberClientService;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.dtos.send.DtosScheduleMember;
import com.project.simplegw.schedule.dtos.send.DtosScheduleSummary;
import com.project.simplegw.schedule.services.ScheduleCountService;
import com.project.simplegw.schedule.services.ScheduleService;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.upload.dtos.DtosAttachements;
import com.project.simplegw.upload.services.AttachmentsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ViewService {   // ViewController에서 필요한 데이터들을 전달하기 위해 여기에 각 서비스들의 필요한 메서드만 선택해 작성한다.
    private final AlarmService alarmService;

    private final MemberService memberService;
    private final MemberClientService memberClientService;
    private final MemberAdminService memberAdminService;

    private final BasecodeService basecodeService;
    private final DocsFormService formService;
    private final AttachmentsService attachmentsService;

    // Boards..
    private final NoticeService noticeService;
    private final FreeboardService freeboardService;
    private final SuggestionService suggestionService;
    private final ArchiveService archiveService;
    private final MinutesService minutesService;

    // Approvals..
    private final DefaultReportService defaultReportService;
    private final DayoffService dayoffService;

    
    // schedule..
    private final ScheduleService scheduleService;
    private final ScheduleCountService scheduleCountService;


    @Autowired
    public ViewService(
        AlarmService alarmService,
        MemberService memberService, MemberClientService memberClientService, MemberAdminService memberAdminService,
        BasecodeService basecodeService, DocsFormService formService, AttachmentsService attachmentsService,

        NoticeService noticeService, FreeboardService freeboardService, SuggestionService suggestionService, ArchiveService archiveService,
        MinutesService minutesService,

        DefaultReportService defaultReportService, DayoffService dayoffService,

        ScheduleService scheduleService, ScheduleCountService scheduleCountService
    ) {
        this.alarmService = alarmService;

        this.memberService = memberService;
        this.memberClientService = memberClientService;
        this.memberAdminService = memberAdminService;

        this.basecodeService = basecodeService;
        this.formService = formService;
        this.attachmentsService = attachmentsService;

        this.noticeService = noticeService;
        this.freeboardService = freeboardService;
        this.suggestionService = suggestionService;
        this.archiveService = archiveService;
        this.minutesService = minutesService;

        this.defaultReportService = defaultReportService;
        this.dayoffService = dayoffService;

        this.scheduleService = scheduleService;
        this.scheduleCountService = scheduleCountService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- alarm ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosAlarm getAlarm(Long id) {
        return alarmService.getAlarm(id);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- alarm ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- member ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public MemberData getMyInfo(LoginUser loginUser) {
        return memberService.getMemberData(loginUser);
    }

    public DtosProfile getProfile(LoginUser loginUser) {
        return memberClientService.getProfile(loginUser);
    }    




    public DtosMemberDetails getMemberDetails(Long memberId) {
        return memberAdminService.getMemberDetails(memberId);
    }

    public DtosMember getMember(Long memberId) {
        return memberAdminService.getMember(memberId);
    }

    public DtosMemberAddOn getMemberAddOn(Long memberId) {
        return memberAdminService.getMemberAddOn(memberId);
    }




    public long workingEmployeeCount() {
        return memberService.workingEmployeeCount();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- member ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- codes ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
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
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- codes ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public String getDocsForm(EditorDocs docs) {
        return formService.getForm(docs);
    }

    public List<DtosAttachements> getAttachmentsList(Long docsId) {
        return attachmentsService.getAttachmentsList(docsId);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- docs common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- board ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosDocs getNotice(Long docsId) {
        return noticeService.getDocs(docsId);
    }

    public DtosDocs getTempNotice(Long docsId) {
        return noticeService.getTempDocs(docsId);
    }


    public DtosDocs getFreeboard(Long docsId) {
        return freeboardService.getDocs(docsId);
    }

    public DtosDocs getTempFreeboard(Long docsId) {
        return freeboardService.getTempDocs(docsId);
    }


    public DtosDocs getSuggestion(Long docsId) {
        return suggestionService.getDocs(docsId);
    }

    public DtosDocs getTempSuggestion(Long docsId) {
        return suggestionService.getTempDocs(docsId);
    }


    public DtosDocs getArchive(Long docsId) {   // 자료실은 임시저장 기능을 제공하지 않는다.
        return archiveService.getDocs(docsId);
    }


    public DtosDocsAddReferrer getMinutes(Long docsId, LoginUser loginUser) {   // 회의록은 임시저장 기능을 제공하지 않는다.
        return minutesService.getDocs(docsId, loginUser);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- board ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosApprovalDocsCommon getDefaultApproval(DocsType docsType, Long docsId, LoginUser loginUser) {
        // 기본 결재문서 형식 외에 디테일이 있는 결재문서에서도 공통으로 사용함.
        // 디테일은 별도 api로 호출하고, 결재문서 기본 데이터만 가져오기 위함.
        return defaultReportService.getDocs(docsType, docsId, loginUser);
    }

    public DtosDocs getTempDefaultApproval(DocsType docsType, Long docsId) {
        return defaultReportService.getTemp(docsType, docsId);
    }



    
    public DtosDayoff getDayoffApproval(Long docsId, LoginUser loginUser) {
        return dayoffService.getDocs(docsId, loginUser);
    }

    public DtosTempDayoff getTempDayoffApproval(Long docsId) {
        return dayoffService.getTempDocs(docsId);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public DtosSchedule getSchedule(Long id) {
        return scheduleService.getSchedule(id);
    }

    public DtosScheduleSummary getTodayScheduleCount() {
        return scheduleCountService.getTodayScheduleCount();
    }

    public List<DtosScheduleMember> getTodayScheduleMemberList() {
        return scheduleCountService.getTodayScheduleMemberList();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
