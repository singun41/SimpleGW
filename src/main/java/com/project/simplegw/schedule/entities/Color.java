package com.project.simplegw.schedule.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.project.simplegw.schedule.vos.ScheduleType;
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
@Table(name = "color")
public class Color extends EntitiesCommon {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_SCHEDULE_TYPE)
    private ScheduleType type;

    @Column(name = "code", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASECODE_CODE)
    private String code;

    @Column(name = "hex_value", nullable = false, updatable = true, length = 6)   // #을 제외한 6자리 hex 색상값
    private String hexValue;



    public Color updateColor(String hexValue) {
        this.hexValue = hexValue.replace("#", "");   // #이 포함되어 들어오면 제거
        return this;
    }
}
