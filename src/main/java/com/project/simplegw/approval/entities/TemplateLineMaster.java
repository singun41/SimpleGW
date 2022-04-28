package com.project.simplegw.approval.entities;

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
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_template_approval_line_master", indexes = @Index(name = "sgw_template_approval_line_master_index_1", columnList = "owner_id"))
public class TemplateLineMaster {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails owner;

    @Column(name = "title", columnDefinition = Constants.COLUMN_DEFINE_TITLE, nullable = false, updatable = false)
    private String title;
}
