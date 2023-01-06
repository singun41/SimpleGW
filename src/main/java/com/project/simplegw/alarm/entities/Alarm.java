package com.project.simplegw.alarm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "alarm", indexes = @Index(columnList = "member_id"))
public class Alarm extends EntitiesCommon {
    
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "alarm_date", nullable = false, updatable = true)
    private LocalDate alarmDate;

    @Column(name = "alarm_time", nullable = false, updatable = true)
    private LocalTime alarmTime;

    @Column(name = "title", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_TITLE)
    private String title;

    @Column(name = "remarks", nullable = true, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_REMARKS)
    private String remarks;

    @Column(name = "created_datetime", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @CreationTimestamp
    private LocalDateTime createdDatetime;




    public Alarm updateAlarmDate(String date) {
        if(date != null )
            this.alarmDate = LocalDate.parse(date);
        return this;
    }

    public Alarm updateAlarmTime(String time) {
        if(time != null)
            this.alarmTime = LocalTime.parse(time);
        return this;
    }

    public Alarm updateTitle(String title) {
        if( title != null && !title.isBlank() )
            this.title = title;
        return this;
    }

    public Alarm updateRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }
}
