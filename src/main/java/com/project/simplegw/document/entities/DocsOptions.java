package com.project.simplegw.document.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.project.simplegw.document.dtos.receive.DtorDocsOptions;
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
@Table(name = "docs_options", indexes = @Index(columnList = "docs_id"))
public class DocsOptions extends EntitiesCommon {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docs docs;

    @Column(name = "due_date", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dueDate;   // 공지사항의 게시 종료일(메인화면에서만 숨기는 용도이며, 공지사항 게시판(리스트)에서는 사용되지 않음)



    @Transient   // entity가 있으면(=테이블에 레코드가 존재하면) 항상 use=true이므로, DB 필드로 설정하지 않는다.
    private boolean use;





    public DocsOptions bindDocs(Docs docs) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.docs = docs;
        return this;
    }
    
    public DocsOptions updateOptions(DtorDocsOptions dto) {
        this.dueDate = LocalDate.parse(dto.getDueDate());
        return this;
    }
}
