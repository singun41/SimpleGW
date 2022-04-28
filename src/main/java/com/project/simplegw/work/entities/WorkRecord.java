package com.project.simplegw.work.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.member.entities.MemberDetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_work_record", indexes = @Index(name = "sgw_work_record_index_1", columnList = "work_date, member_id"))
public class WorkRecord {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_date", columnDefinition = Constants.COLUMN_DEFINE_DATE, nullable = false, updatable = false)
    private LocalDate workDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails memberDetails;

    @Column(name = "today_work", columnDefinition = "nvarchar(500)")
    private String todayWork;

    @Column(name = "next_work_plan", columnDefinition = "nvarchar(500)")
    private String nextWorkPlan;

    @Column(name = "team", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    public WorkRecord insertMemberDetails(MemberDetails memberDetails) {
        this.memberDetails = memberDetails;
        this.team = memberDetails.getTeam();
        this.jobTitle = memberDetails.getJobTitle();
        this.name = memberDetails.getName();
        return this;
    }

    public WorkRecord updateTodayWork(String todayWork) {
        this.todayWork = todayWork;
        return this;
    }
    public WorkRecord updateNextWorkPlan(String nextWorkPlan) {
        this.nextWorkPlan = nextWorkPlan;
        return this;
    }
}
