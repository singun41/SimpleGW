package com.project.simplegw.document.approval.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.member.data.MemberData;
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
@Table(name = "referrer", indexes = {
    @Index(columnList = "docs_id"),
    @Index(columnList = "member_id, docs_id")   // 수신한 결재문서를 날짜로 검색할 때 사용. index 테스트 완료.
})
public class Referrer extends EntitiesCommon {   // 결재라인의 참조자 정보

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "team", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Column(name = "checked_datetime", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime checkedDatetime;





    public Referrer setReferrer(MemberData memberData) {
        this.memberId = memberData.getId();
        this.team = memberData.getTeam();
        this.jobTitle = memberData.getJobTitle();
        this.name = memberData.getName();
        return this;
    }

    public Referrer checked() {
        this.checkedDatetime = LocalDateTime.now();
        return this;
    }
}
