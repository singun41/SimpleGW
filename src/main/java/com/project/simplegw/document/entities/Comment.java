package com.project.simplegw.document.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
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
@ToString(callSuper = true, exclude = {"docs", "comment"})   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다. content는 내용이 많으므로 제외.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "docs_comment", indexes = @Index(columnList = "docs_id"))
public class Comment extends EntitiesCommon {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "writer_id", nullable = false, updatable = false)
    private Long writerId;   // 작성자를 찾기 위해서 Member 클래스를 바인딩해두면 소유자 검증할 때마다 Member를 찾는 쿼리가 발생함. id값으로만 해두면 equals 한 번에 처리가 가능함. 성능이점이 있음.

    @Column(name = "writer_team", nullable = false,  updatable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String writerTeam;

    @Column(name = "writer_job_title", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String writerJobTitle;

    @Column(name = "writer_name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String writerName;

    @Column(name = "comment", nullable = true, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_COMMENT)
    private String comment;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    


    
    public Comment bindDocs(Docs docs) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.docs = docs;
        return this;
    }

    public Comment setMemberData(MemberData memberData) {
        this.writerId = memberData.getId();
        this.writerTeam = memberData.getTeam();
        this.writerJobTitle = memberData.getJobTitle();
        this.writerName = memberData.getName();
        return this;
    }
}
