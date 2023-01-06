package com.project.simplegw.schedule.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.schedule.dtos.receive.DtorSchedule;
import com.project.simplegw.schedule.dtos.receive.DtorScheduleIncludedTime;
import com.project.simplegw.schedule.vos.ScheduleType;
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
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "schedule", indexes = @Index(columnList = "date_from"))
public class Schedule extends EntitiesCommon {
    
    @Column(name = "date_from", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateFrom;

    @Column(name = "time_from", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TIME)
    private LocalTime timeFrom;

    @Column(name = "date_to", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateTo;

    @Column(name = "time_to", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TIME)
    private LocalTime timeTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_SCHEDULE_TYPE)
    private ScheduleType type;

    @Column(name = "code", nullable = false, updatable = true, length = Constants.COLUMN_LENGTH_BASECODE_CODE)
    private String code;

    @Column(name = "title", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TITLE)
    private String title;

    @Column(name = "content", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String content;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "team", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Column(name = "created_datetime", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;




    
    public Schedule updateType(ScheduleType type) {
        this.type = type;
        return this;
    }

    public Schedule setMemberData(MemberData memberData) {
        this.memberId = memberData.getId();
        this.team = memberData.getTeam();
        this.jobTitle = memberData.getJobTitle();
        this.name = memberData.getName();
        return this;
    }

    public Schedule updateData(DtorSchedule dto) {
        this.dateFrom = dto.getDateFrom();
        this.dateTo = dto.getDateTo();

        this.code = dto.getCode();
        this.title = dto.getTitle();

        if(dto.getContent() == null || dto.getContent().isBlank())
            this.content = null;
        else
            this.content = dto.getContent();

        if(dto instanceof DtorScheduleIncludedTime timeDto) {
            this.timeFrom = timeDto.getTimeFrom();
            this.timeTo = timeDto.getTimeTo();
        }

        return this;
    }
}
