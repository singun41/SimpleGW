package com.project.simplegw.document.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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
@Table(name = "sgw_attachments", indexes = @Index(name = "sgw_attachments_index_1", columnList = "docs_id, seq"))
public class Attachments {
    
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 문서가 삭제되어도 첨부파일은 그대로 유지하기 위해 JoinColumn으로 작성하지 않는다.
    // JoinColumn을 하게되면 FK가 걸리게 되고, 문서 삭제를 제대로 진행하려면 첨부파일도 삭제되어야 한다.
    // 따라서 @JoinColumn으로 설정하지 않는다.
    @Column(name = "docs_id")
    private Long docsId;

    @Column(name = "seq", nullable = false, updatable = false)   // document 내에서 첨부파일의 순번
    private int seq;

    @Column(name = "conversion_name", columnDefinition = "uniqueidentifier", nullable = false, updatable = false)
    private String conversionName;

    @Column(name = "original_name", columnDefinition = "nvarchar(200)", nullable = false, updatable = false)
    private String originalName;

    @Column(name = "path", length = 20, nullable = false, updatable = false)
    private String path;
}
