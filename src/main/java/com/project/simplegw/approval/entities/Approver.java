package com.project.simplegw.approval.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.approval.vos.ApproverStatus;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.member.entities.MemberDetails;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_approver", indexes = {
    @Index(name = "sgw_approver_index_1", columnList = "docs_id"),   // 결재 문서 수정시 결재라인을 삭제하고 다시 입력하는데, 문서번호로 삭제하기 위해 인덱스 추가
    @Index(name = "sgw_approver_index_2", columnList = "approver_id")    // 결재요청 받은 문서 보기, where 절에 status 조건을 추가하면 현재 결재요청 문서, 결재 요청받은 모든 문서 둘 다 가능함.
})
public class Approver {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document docs;

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approver_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails approver;

    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false)
    private String team;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false)
    private String jobTitle;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = Constants.COLUMN_LENGTH_APPROVER_STATUS, nullable = false)
    private ApproverStatus status;

    @Column(name = "checked_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime checkedDatetime;

    public Approver insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }

    public Approver insertApprover(MemberDetails details) {
        this.approver = details;
        this.team = details.getTeam();
        this.jobTitle = details.getJobTitle();
        this.name = details.getName();
        return this;
    }

    public Approver updateStatus(ApproverStatus status) {
        if(this.seq > 0) {
            switch(status) {
                case CONFIRMED:
                case REJECTED:
                    this.status = status;
                    this.checkedDatetime = LocalDateTime.now();
                    break;
                default:
                    break;
            }
        }
        return this;
    }

    public Approver updateStatusToProceed() {   // 이전 결재자가 승인 후 다음번 결재자의 상태를 WAITING --> PROCEED로 변경해줘야 한다.
        if(this.status.equals(ApproverStatus.WAITING)) {
            this.status = ApproverStatus.PROCEED;
        }
        return this;
    }
}
