package com.project.simplegw.approval.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.entities.Document;
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
@Table(name = "sgw_approval_overtime", indexes = @Index(name = "sgw_approval_overtime_index_1", columnList = "docs_id, seq"))
public class Overtime extends SubListEntity <Overtime> {
    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "code", nullable = false, length = Constants.COLUMN_LENGTH_BASE_CODE)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails member;

    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false)
    private String team;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false)
    private String jobTitle;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false)
    private String name;

    @Column(name = "work_date", columnDefinition = Constants.COLUMN_DEFINE_DATE, nullable = false)
    private LocalDate workDate;

    @Column(name = "time_start", columnDefinition = Constants.COLUMN_DEFINE_TIME, nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", columnDefinition = Constants.COLUMN_DEFINE_TIME, nullable = false)
    private LocalTime timeEnd;

    @Column(name = "remarks", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String remarks;

    @Override
    public Overtime insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }
    
    public Overtime insertMember(MemberDetails member) {
        this.member = member;
        this.team = member.getTeam();
        this.jobTitle = member.getJobTitle();
        this.name = member.getName();
        return this;
    }
}
