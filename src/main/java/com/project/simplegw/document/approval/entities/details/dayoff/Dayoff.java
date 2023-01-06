package com.project.simplegw.document.approval.entities.details.dayoff;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.document.approval.entities.details.DetailsCommon;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
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
@Table(name = "approval_dayoff", indexes = @Index(columnList = "docs_id"))
public class Dayoff extends DetailsCommon <Dayoff> {
    // update는 삭제 후 insert로 처리하므로 updatable을 false로 설정.

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "code", nullable = false, updatable = false, length = Constants.COLUMN_LENGTH_BASECODE_CODE)
    private String code;

    @Column(name = "date_from", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false, updatable = false, columnDefinition = Constants.COLUMN_DEFINE_DATE)
    private LocalDate dateTo;

    @Column(name = "duration", nullable = false, updatable = false)
    private int duration;

    // 연차 = 1, 반차 = 0.5 등 소수점 계산을 위해
    // MS-SQL의 float이 java의 double로 매핑되어 double로 처리.
    @Column(name = "count", nullable = false, updatable = false)
    private double count;


    @Override
    public Dayoff bindDocs(Docs docs) {   // 연관관계 매핑 메서드는 bind엔티티명 으로 작성한다.
        this.docs = docs;
        return this;
    }

    public Dayoff updateSeq(int seq) {
        this.seq = seq;
        return this;
    }

    public Dayoff updateDuration() {
        this.duration = Period.between(this.dateFrom, this.dateTo).getDays() + 1;   // end가 제외되어 +1 해준다.
        this.count = this.duration;

        LocalDate dt = LocalDate.from(this.dateFrom);
        while(dt.isBefore(this.dateTo) || dt.isEqual(this.dateTo)) {   // dateTo 포함.
            // from ~ to 사이의 주말은 제외한다.
            if(dt.getDayOfWeek() == DayOfWeek.SATURDAY || dt.getDayOfWeek() == DayOfWeek.SUNDAY)
                this.count--;
            
            dt = dt.plusDays(1L);
        }

        return updateCount();
    }

    private Dayoff updateCount() {
        // 결재문서 최종 승인시 스케줄에 자동 등록되므로 고정코드로 변경.

        if(this.code.equals( ScheduleFixedPersonalCode.DAYOFF.getCode() )) {
            // 연차는 updateDuration()에서 계산한 그대로 적용.

        } else if( this.code.equals( ScheduleFixedPersonalCode.HALF_AM.getCode() ) || this.code.equals( ScheduleFixedPersonalCode.HALF_PM.getCode() ) ) {
            this.count = this.count / 2.0;   // BasecodeService에서 등록하는 반차 코드, 반차는 하루당 연차 카운트의 0.5개

        } else {
            this.count = this.count * 0;   // 나머지는 계산하지 않음.
        }
        
        return this;
    }
}
