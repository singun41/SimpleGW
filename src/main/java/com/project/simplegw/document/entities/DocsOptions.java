package com.project.simplegw.document.entities;

import java.time.LocalDate;

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
import javax.persistence.Transient;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.dtos.DocsOptionsDTO;

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
@Table(name = "sgw_docs_options", indexes = @Index(name = "sgw_docs_options_index_1", columnList = "docs_id"))
public class DocsOptions {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docs_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document docs;

    @Transient   // entity가 있으면 항상 use=true이므로, DB 필드로 설정하지 않는다.
    private boolean use;

    @Column(name = "due_date", columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dueDate;

    public DocsOptions insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }
    public DocsOptions updateOptions(DocsOptionsDTO dto) {
        this.dueDate = dto.getDueDate();
        return this;
    }
}
