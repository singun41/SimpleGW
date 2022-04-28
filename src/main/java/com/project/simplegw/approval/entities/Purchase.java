package com.project.simplegw.approval.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.entities.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_approval_purchase", indexes = @Index(name = "sgw_approval_purchase_index_1", columnList = "docs_id, seq"))
public class Purchase extends SubListEntity <Purchase> {
    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "item_name", columnDefinition = Constants.COLUMN_DEFINE_REMARKS, nullable = false)
    private String itemName;

    @Column(name = "item_spec", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String itemSpec;

    @Column(name = "due_date", columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dueDate;

    @Column(name = "store", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String store;

    @Column(name = "url", columnDefinition = "nvarchar(500)")
    private String url;

    @Column(name = "price")
    private int price;

    @Column(name = "qty")
    private int qty;

    @Column(name = "sum")
    private int sum;

    @Override
    public Purchase insertDocs(Document docs) {
        this.docs = docs;
        calculateSum();
        setEmptyValueToNull();
        return this;
    }
    private void calculateSum() {
        this.sum = this.price * this.qty;
    }
    private void setEmptyValueToNull() {
        this.itemSpec = (this.itemSpec.isBlank() ? null : this.itemSpec);
        this.store = (this.store.isBlank() ? null : this.store);
        this.url = (this.url.isBlank() ? null : this.url);
    }
}
