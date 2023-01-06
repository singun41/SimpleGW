package com.project.simplegw.document.approval.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true, exclude = "docs")   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "approval_status", indexes = @Index(columnList = "docs_id"))
public class ApprovalStatus extends EntitiesCommon {   // 결재문서의 최종 상태 엔티티
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // 각 결재문서당 1개의 레코드만 있어야 하므로 unique를 설정한다.
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "member_id", nullable = false, updatable = true)
    private Long memberId;

    @Column(name = "team", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign", nullable = false, updatable = true, length = Constants.COLUMN_LENGTH_APPROVAL_SIGN_TYPE)
    private Sign sign;

    @Column(name = "seq_now", nullable = false, updatable = true)
    private int seqNow;   // 현재 결재자의 순번
    
    @Column(name = "seq_last", nullable = false, updatable = true)
    private int seqLast;   // 마지막 결재자의 순번

    @Column(name = "finished", nullable = false, updatable = true)
    private boolean finished;   // 결재문서 완결 여부, 중간 순번에서 반려가 되도 true로 처리. 문서가 완결됐는지 판별하는데 필요한 필드.



    public ApprovalStatus bindDocs(Docs docs) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.docs = docs;
        return this;
    }

    public ApprovalStatus init(List<DtosApprover> approvers) {
        DtosApprover currentApprover = approvers.get(0);

        this.memberId = currentApprover.getMemberId();
        this.team = currentApprover.getTeam();
        this.jobTitle = currentApprover.getJobTitle();
        this.name = currentApprover.getName();
        
        this.sign = Sign.PROCEED;
        this.seqLast = approvers.size() - 1;

        return this;
    }

    public ApprovalStatus update(List<DtosApprover> approvers) {
        DtosApprover currentApprover = approvers.get(this.seqNow);

        if(currentApprover.getSign() == Sign.REJECTED) {
            rejected();

        } else {
            if(this.seqNow < this.seqLast)
                next(approvers);
            else
                confirmed();
        }
        return this;
    }

    private void next(List<DtosApprover> approvers) {
        DtosApprover nextApprover = approvers.get( ++this.seqNow );
        
        this.memberId = nextApprover.getMemberId();
        this.team = nextApprover.getTeam();
        this.jobTitle = nextApprover.getJobTitle();
        this.name = nextApprover.getName();
    }

    private void confirmed() {
        if(this.seqNow == this.seqLast) {
            this.sign = Sign.CONFIRMED;
            this.finished = true;
        }
    }

    private void rejected() {
        this.sign = Sign.REJECTED;
        this.finished = true;
    }
}
