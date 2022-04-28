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

import com.project.simplegw.approval.vos.ApproverRole;
import com.project.simplegw.common.vos.Constants;
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
@Table(name = "sgw_template_approval_line_details", indexes = @Index(name = "sgw_template_approval_line_details_index_1", columnList = "master_id"))
public class TemplateLineDetails {   // 자주 사용하는 결재라인을 저장하기 위한 템플릿
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "master_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TemplateLineMaster master;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = Constants.COLUMN_LENGTH_ROLE, nullable = false, updatable = false)
    private ApproverRole role;   // approver or referrer

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;       // submitter = 0, approver = 1~n, referrer = 0

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails member; // 결재 순번에 지정된 멤버id
}
