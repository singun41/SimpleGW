package com.project.simplegw.upload.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "attachments", indexes = @Index(columnList = "docs_id"))
public class Attachments extends EntitiesCommon {
    
    // 문서가 삭제되어도 첨부파일 정보는 그대로 유지하기 위해 JoinColumn으로 작성하지 않는다.
    @Column(name = "docs_id", nullable = false, updatable = false)   // nullable = true  -->  false 로 변경
    private Long docsId;

    @Column(name = "seq", nullable = false, updatable = false)   // document 내에서 첨부파일의 순번
    private int seq;

    @Column(name = "conversion_name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_UNIQUE_IDENTIFIER)
    private String conversionName;

    @Column(name = "original_name", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_FILE_NAME)
    private String originalName;

    @Column(name = "path", nullable = false, updatable = false, length = 20)   // 파일이 업로드 되어 있는 디렉토리를 찾기 위해 추가한 날짜형태 필드: yyyy/M/d ('월'과 '일'은 1자리 또는 2자리이다.)
    private String path;


    
    public Attachments insertDocsId(Docs docs) {   // 연관관계가 아니기 때문에 bind엔티티명 으로 작성하지 않는다.
        if(docs == null || docs.getId() == null)
            return this;
        
        this.docsId = docs.getId();
        return this;
    }
}
