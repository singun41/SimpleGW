package com.project.simplegw.schedule.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.schedule.vos.ScheduleType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_schedule", indexes = @Index(name = "sgw_schedule_index_1", columnList = "type, year, month, week_of_year"))
public class Schedule {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASE_CODE_TYPE)
    private ScheduleType type;

    @Column(name = "code", nullable = false, length = Constants.COLUMN_LENGTH_BASE_CODE)
    private String code;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false, updatable = false)
    private String team;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false, updatable = false)
    private String jobTitle;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false, updatable = false)
    private String name;

    @Column(name = "title", columnDefinition = Constants.COLUMN_DEFINE_TITLE, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String content;
    
    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Column(name = "week_of_year")
    private int weekOfYear;

    @Column(name = "datetime_start", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime datetimeStart;

    @Column(name = "datetime_end", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime datetimeEnd;

    @Column(name = "created_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    public Schedule setWriter(MemberDetails member) {
        this.memberId = member.getId();
        this.team = member.getTeam();
        this.jobTitle = member.getJobTitle();
        this.name = member.getName();
        return this;
    }

    public Schedule updateTitle(String title) {
        if(title != null && !title.strip().isBlank()) {
            this.title = title.strip();
        }
        return this;
    }

    public Schedule updateContent(String content) {
        if(content != null && !content.strip().isBlank()) {
            content = content.strip();
        }
        this.content = content;
        return this;
    }

    public Schedule updateCode(String code) {
        if(code != null && !code.strip().isBlank()) {
            this.code = code;
        }
        return this;
    }

    public Schedule updateStart(LocalDateTime datetimeStart) {
        this.datetimeStart = datetimeStart;
        return this;
    }

    public Schedule updateEnd(LocalDateTime datetimeEnd) {
        this.datetimeEnd = datetimeEnd;
        return this;
    }

    public Schedule updateYearAndMonth() {   // 시작일 기준으로 연도와 월 값을 업데이트 해준다. 캘린더에 렌더링할 데이터를 일괄 검색할 때 year와 month가 필요함.
        if(this.datetimeStart != null) {
            this.year = this.datetimeStart.getYear();
            this.month = this.datetimeStart.getMonthValue();
            this.weekOfYear = this.datetimeStart.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        }
        return this;
    }
}
