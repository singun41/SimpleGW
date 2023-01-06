package com.project.simplegw.document.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true, exclude = {"docs", "content"})   // lazy loading 이기 때문에 제외하지 않으면 no session 에러가 난다. content는 내용이 많으므로 제외.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "docs_content", indexes = @Index(columnList = "docs_id"))
public class Content extends EntitiesCommon {   // 문서의 내용은 대부분 데이터가 크기 때문에 필요할 때에만 가져오도록 하기 위해 별도 클래스로 분리한다.

    // Member <--> MemberDetails의 관계처럼 Cascade를 설정하지 않는 이유는 Document를 먼저 저장하고 반환된 Entity를 이용해서, 결재문서에도 이용하기 위함이다.
    // OneToOne은 잘못 사용하면 문제가 많기 때문에 가장 안전한 ManyToOne으로 대체한다.
    // 대신 항상 Document를 먼저 저장 후 리턴값을 받아와서 DocuContent를 저장하도록 서비스를 설정해야 한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "content", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_CONTENT)
    private String content;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;




    
    public Content bindDocs(Docs docs) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.docs = docs;
        return this;
    }
    
    public Content updateContent(String content) {
        if(content != null && !content.strip().isBlank())
            this.content = content;
        else
            this.content = null;
        
        return this;
    }
}
