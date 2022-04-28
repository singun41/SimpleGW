package com.project.simplegw.common.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "sgw_basecode", indexes = @Index(name = "sgw_basecode_index_1", columnList = "type, code"))
public class Basecode {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASE_CODE_TYPE)
    private BasecodeType type;

    @Column(name = "code", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASE_CODE)
    private String code;

    @Column(name = "value", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_BASE_CODE_VALUE)
    private String value;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "remarks", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String remarks;

    @Column(name = "seq", nullable = false)
    private int seq;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    public Basecode updateValue(String value) {
        this.value = value.strip();
        return this;
    }

    public Basecode changeToEnabled() {
        this.enabled = true;
        return this;
    }
    public Basecode changeToDisabled() {
        this.enabled = false;
        return this;
    }
    public Basecode updateRemarks(String remarks) {
        if(remarks != null && !remarks.isBlank()) {
            this.remarks = remarks.strip();
        }
        return this;
    }
    public Basecode updateSeq(int seq) {
        this.seq = seq;
        return this;
    }
}
