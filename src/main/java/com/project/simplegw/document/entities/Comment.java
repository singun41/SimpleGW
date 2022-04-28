package com.project.simplegw.document.entities;

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
@ToString(exclude = "comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_docs_comment", indexes = @Index(name = "sgw_docs_comment_index_1", columnList = "docs_id"))
public class Comment {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document docs;

    @Column(name = "writer_id", nullable = false, updatable = false)   // memberDetails의 id
    private Long writerId;

    @Column(name = "writer_team", columnDefinition = Constants.COLUMN_DEFINE_TEAM, updatable = false)
    private String writerTeam;

    @Column(name = "writer_job_title", columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE, updatable = false)
    private String writerJobTitle;

    @Column(name = "writer_name", columnDefinition = Constants.COLUMN_DEFINE_NAME, updatable = false)
    private String writerName;

    @Column(name = "comment", columnDefinition = Constants.COLUMN_DEFINE_COMMENT, updatable = false)   // 200자 이하
    private String comment;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;


    public Comment insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }
}
