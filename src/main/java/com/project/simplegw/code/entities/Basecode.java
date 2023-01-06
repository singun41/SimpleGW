package com.project.simplegw.code.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.system.entities.EntitiesCommon;
import com.project.simplegw.system.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "basecode", indexes = @Index(columnList = "type"))
public class Basecode extends EntitiesCommon {
    
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASECODE_TYPE)
    @Enumerated(EnumType.STRING)
    private BasecodeType type;

    @Column(name = "code", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASECODE_CODE)
    private String code;

    @Column(name = "value", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_BASECODE_VALUE)
    private String value;

    @Column(name = "enabled", nullable = false, updatable = true)
    private boolean enabled;

    @Column(name = "remarks", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String remarks;

    @Column(name = "seq", nullable = false, updatable = true)
    private int seq;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;



    // 컨셉: code 값 자체의 업데이트는 불가. 변경하려면 기존 코드 disable 처리 후 신규 생성.
    public Basecode setType(BasecodeType type) {
        this.type = type;
        return this;
    }

    public Basecode setEnabled() {
        this.enabled = true;
        return this;
    }

    public Basecode setDisabled() {
        this.enabled = false;
        return this;
    }

    public Basecode updateSeq(int seq) {
        this.seq = seq;
        return this;
    }

    public Basecode updateValue(String value) {
        this.value = value;
        return this;
    }

    public Basecode updateRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }
}
