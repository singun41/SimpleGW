package com.project.simplegw.schedule.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "schedule_count", indexes = {@Index(columnList = "date"), @Index(columnList = "schedule_id")})
public class ScheduleCount extends EntitiesCommon {

    @Column(name = "date", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = false, updatable = false, unique = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "code", nullable = false, updatable = true, length = Constants.COLUMN_LENGTH_BASECODE_CODE)
    private String code;




    public ScheduleCount updateCode(Schedule schedule) {
        this.code = schedule.getCode();
        return this;
    }
}
