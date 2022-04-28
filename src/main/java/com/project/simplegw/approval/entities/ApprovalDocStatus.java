package com.project.simplegw.approval.entities;

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
// @ToString(exclude = "docu")
@Entity
@Table(name = "sgw_approval_docs_status", indexes = {
    // docs_id로 문서를 찾고, 작성자 바로 다음인 첫 결재자가 진행을 했는지 여부를 확인하여 문서 수정을 체크한다.
    @Index(name = "sgw_approval_docs_status_index_1", columnList = "docs_id"),

    // is_finished를 인덱스로 추가해두면 결재문서의 상태가 변경될 때마다 DB에서 index 재작업을 하게 되므로 제외한다.
    // 인덱스 2: 작성한 결재문서 중 진행/완결 리스트  -->  where 조건: is_finished = false/true
    @Index(name = "sgw_approval_docs_status_index_2", columnList = "writer_id")

    // 결재문서 상태를 보기 위한 엔티티이고, 결재자가 받은 문서 리스트는 Approver 엔티티로 처리한다.
    // 인덱스 3: 결재자가 받은 문서  -->  where 조건: approver_id, status = proceed, is_finished = false
    // @Index(name = "sgw_approval-docs_status_index_3", columnList = "approver_id")
})
public class ApprovalDocStatus {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)  // one to one으로 설정해야 하지만, 성능 및 편의를 위해 many to one으로 한다.
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document docs;

    @Column(name = "writer_id")
    private Long writerId;

    @Column(name = "approver_count")
    private int approverCount;

    @Column(name = "approver_seq")
    private int approverSeq;

    @Column(name = "approver_id")
    private Long approverId;   // 현재 결재자의 memberId

    // 연결된 결재문서의 현재 상태를 나타내므로, 결재자 순서가 이동함에 따라 업데이트가 되어야 한다. updatable = true
    // builder로 빌드하고 나서 다음 결재자를 insert 하기 위해서 nullable = false를 해제한다.
    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM/* , nullable = false */)
    private String team;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME/* , nullable = false */)
    private String name;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE/* , nullable = false */)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = Constants.COLUMN_LENGTH_APPROVER_STATUS, nullable = false)
    private ApproverStatus status;      //  결재문서의 최종 승인 / 반려 값

    @Column(name = "is_finished")
    private boolean finished;   // 결재문서의 진행/완결 여부


    public ApprovalDocStatus updateWriter(Long writerId) {
        this.writerId = writerId;
        return this;
    }
    public ApprovalDocStatus updateNextApprover(Approver approver) {
        MemberDetails memberDetails = approver.getApprover();
        
        if(this.approverSeq < this.approverCount) {
            this.approverId = memberDetails.getId();
            this.team = memberDetails.getTeam();
            this.name = memberDetails.getName();
            this.jobTitle = memberDetails.getJobTitle();
            this.approverSeq++;
        }
        if(this.approverSeq == this.approverCount) {
            updateFinish(approver);
        }
        return this;
    }
    public ApprovalDocStatus updateFinish(Approver approver) {  // 마지막 결재자의 결재정보로 업데이트한다.
        switch(approver.getStatus()) {
            // 아래 두 가지 경우에만 업데이트할 수 있다.

            case CONFIRMED:
                if(approver.getSeq() == this.approverCount) {   // 승인은 최종 결재자가 승인한 경우만 finish.
                    this.finished = true;
                    this.status = approver.getStatus();
                }
                break;

            case REJECTED:
                this.status = approver.getStatus();
                this.finished = true;   // 중간 또는 최종 결재자가 반려한 경우는 즉시 finish.
                break;

            default:
                break;
        }
        return this;
    }
}
