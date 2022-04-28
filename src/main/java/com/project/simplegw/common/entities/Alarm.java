package com.project.simplegw.common.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;

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
@Table(name = "sgw_alarm", indexes = @Index(name = "sgw_alarm_index_1", columnList = "member_id"))
public class Alarm {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "content", columnDefinition = Constants.COLUMN_DEFINE_REMARKS)   // 간단한 내용을 작성하므로, remarks 길이로 설정
    private String content;

    @Column(name = "checked_datetime", columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    private LocalDateTime checkedDatetime;

    public Alarm updateCheckedDatetime() {
        this.checkedDatetime = LocalDateTime.now();
        return this;
    }
}
