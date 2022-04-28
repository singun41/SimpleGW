package com.project.simplegw.approval.entities;

import java.time.LocalDateTime;

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
@Table(name = "sgw_referrer", indexes = {
    // Approver 엔티티와 동일한 인덱스 설정.
    @Index(name = "sgw_referrer_index_1", columnList = "docs_id"),
    @Index(name = "sgw_referrer_index_2", columnList = "referrer_id")
})
public class Referrer {
    // 결재 문서 참조자 리스트
    // 참조 = 문서를 볼 수 있는 권한, 공유 기능과 같으므로 회의록의 공유 기능으로도 사용할 수 있다.
    // 처음에는 ApprovalLine으로 Approver와 Referrer를 하나의 엔티티로 묶었으나, 일반 문서의 공유 기능으로도 사용할 수 있어서 엔티티를 나눈다.

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document docs;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "referrer_id", referencedColumnName = "id", nullable = false, updatable = false)
    private MemberDetails referrer;

    @Column(name = "team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, nullable = false)
    private String team;

    @Column(name = "job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, nullable = false)
    private String jobTitle;

    @Column(name = "name", columnDefinition = Constants.COLUMN_DEFINE_NAME, nullable = false)
    private String name;

    @Column(name = "checked_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime checkedDatetime;

    public Referrer insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }

    public Referrer insertReferrer(MemberDetails details) {
        this.referrer = details;
        this.team = details.getTeam();
        this.jobTitle = details.getJobTitle();
        this.name = details.getName();
        return this;
    }

    public Referrer updateCheckedDatetime() {
        this.checkedDatetime = LocalDateTime.now();
        return this;
    }
}
