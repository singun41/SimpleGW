package com.project.simplegw.document.approval.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.document.approval.dtos.send.DtosApprover;
import com.project.simplegw.document.approval.vos.Sign;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.system.entities.EntitiesCommon;

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
@Table(name = "ongoing_approval")
public class OngoingApproval extends EntitiesCommon {
    /*
        결재문서 등록자 입장에서 --> 진행중인 결재문서 카운트 및 리스트
        결재자 입장에서 --> 결재 요청 문서 카운트 및 리스트

        결재문서가 진행되면 이 테이블에 등록, 완결시 삭제.
        수시로 insert, delete가 일어나므로 인덱스를 설정하지 않는다.


        
        - 이 엔티티를 사용하는 이유

        ApprovalStatus만 사용해서 진행중인 문서, 나에게 도착한 결재 요청 문서를 카운트할 때 소량의 데이터라면 문제가 없다.
        하지만 시간이 지날수록 데이터가 쌓이게 되면, 검색 퍼포먼스가 커지게 된다.
        
        이 엔티티(테이블)은 일회성으로 결재문서 등록시 같이 등록되고 최종 결재(승인 or 반려)가 결정되면 삭제된다.
        그래서 검색 퍼포먼스가 증가하지 않게 된다.
    */
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // 각 결재문서당 1개의 레코드만 있어야 하므로 unique를 설정한다.
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private Long ownerId;

    @Column(name = "approver_id", nullable = false, updatable = true)   // 결재자 순서에 따라 업데이트 가능해야 하므로 updatable true
    private Long approverId;

    @Column(name = "approver_seq", nullable = false, updatable = true)   // 결재자 순서에 따라 업데이트 가능해야 하므로 updatable true
    private int approverSeq;


    public OngoingApproval update(List<DtosApprover> approvers) {
        DtosApprover approver = approvers.stream().filter(e -> e.getSign() == Sign.PROCEED).findFirst().get();
        this.approverId = approver.getMemberId();
        this.approverSeq = approvers.indexOf(approver);
        return this;
    }
}
