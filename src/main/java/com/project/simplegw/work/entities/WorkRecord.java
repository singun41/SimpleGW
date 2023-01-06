package com.project.simplegw.work.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.member.data.MemberData;
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
@Table(name = "work_record", indexes = @Index(columnList = "work_date"))
public class WorkRecord extends EntitiesCommon {

    @Column(name = "work_date", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate workDate;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "team", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Column(name = "today_work", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_COMMENT)
    private String todayWork;

    @Column(name = "next_plan", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_COMMENT)
    private String nextPlan;




    public WorkRecord setMemberData(MemberData memberData) {
        this.memberId = memberData.getId();
        this.team = memberData.getTeam();
        this.jobTitle = memberData.getJobTitle();
        this.name = memberData.getName();
        return this;
    }

    public WorkRecord updateWorkDate(String date) {
        this.workDate = LocalDate.parse(date);
        return this;
    }

    public WorkRecord updateTodayWork(String content) {
        if(content == null || content.isBlank())
            this.todayWork = null;
        else
            this.todayWork = content;
        return this;
    }
    
    public WorkRecord updateNextPlan(String content) {
        if(content == null || content.isBlank())
            this.nextPlan = null;
        else
            this.nextPlan = content;
        return this;
    }
}
