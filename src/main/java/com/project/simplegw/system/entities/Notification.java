package com.project.simplegw.system.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "notification", indexes = @Index(columnList = "member_id"))
public class Notification extends EntitiesCommon {

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "content", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_REMARKS)   // 간단한 내용이므로 비고 컬럼으로 처리.
    private String content;

    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    @CreationTimestamp
    private LocalDate createdDate;

    @Column(name = "checked", nullable = true, updatable = true)
    private boolean checked;



    public Notification updateChecked() {
        this.checked = true;
        return this;
    }
}
