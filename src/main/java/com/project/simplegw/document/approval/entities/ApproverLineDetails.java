package com.project.simplegw.document.approval.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.document.approval.vos.ApprovalRole;
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
@ToString(callSuper = true, exclude = "master")   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "approver_line_details", indexes = @Index(columnList = "master_id"))
public class ApproverLineDetails extends EntitiesCommon {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "master_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ApproverLine master;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_ROLE)
    private ApprovalRole role;

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;   // approver = 1~n, referrer = 0

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;   // 결재 순번에 지정된 Member Id
}
