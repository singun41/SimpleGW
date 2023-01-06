package com.project.simplegw.member.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.project.simplegw.member.dtos.receive.DtorEnvSetting;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true, exclude = "member")   // lazy loading 일 때 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member_environment_setting", indexes = @Index(columnList = "member_id"))
public class MemberEnvSetting extends EntitiesCommon {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "sys_noti_del_day", nullable = false, updatable = true)
    private int sysNotiDelDay;   // 값 이전에 받은 시스템 알림을 모두 삭제. NotificationRepo 클래스에 네이티브 쿼리로 참고하고 있음.

    @Column(name = "at_once_approver", nullable = false, updatable = true)
    private boolean atOnceApprover;   // 결재 요청문서 수신시 알림 팝업 여부

    @Column(name = "at_once_referrer", nullable = false, updatable = true)
    private boolean atOnceReferrer;   // 결재 참조문서 수신시 알림 팝업 여부

    @Column(name = "at_once_approval", nullable = false, updatable = true)
    private boolean atOnceApproval;   // 결재 최종승인/반려시 알림 팝업 여부
    
    @Column(name = "main_calendar_mine", nullable = false, updatable = true)
    private boolean mainCalendarMine;   // 메인 페이지 캘린더에 내 일정 표시하기

    @Column(name = "main_calendar_team", nullable = false, updatable = true)
    private boolean mainCalendarTeam;   // 메인 페이지 캘린더에 팀원 일정 표시하기

    @Column(name = "calendar_holiday", nullable = false, updatable = true)
    private boolean calendarHoliday;   // 메인 페이지 캘린더에 공휴일 표시하기




    public MemberEnvSetting init() {
        this.sysNotiDelDay = Constants.NOTIFICATION_STORED_DEFAULT_DURATION;
        
        this.atOnceApprover = true;
        this.atOnceReferrer = false;
        this.atOnceApproval = true;

        this.mainCalendarMine = true;
        this.mainCalendarTeam = false;
        this.calendarHoliday = true;

        return this;
    }

    
    public MemberEnvSetting update(DtorEnvSetting dto) {
        this.sysNotiDelDay = dto.getSysNotiDelDay();
        
        this.atOnceApprover = dto.isAtOnceApprover();
        this.atOnceReferrer = dto.isAtOnceReferrer();
        this.atOnceApproval = dto.isAtOnceApproval();

        this.mainCalendarMine = dto.isMainCalendarMine();
        this.mainCalendarTeam = dto.isMainCalendarTeam();
        this.calendarHoliday = dto.isCalendarHoliday();

        return this;
    }
}
