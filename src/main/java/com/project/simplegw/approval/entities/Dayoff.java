package com.project.simplegw.approval.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.entities.Document;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_approval_dayoff", indexes = @Index(name = "sgw_approval_dayoff_index_1", columnList = "docs_id, seq"))
public class Dayoff extends SubListEntity <Dayoff> {
    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "code", nullable = false, length = Constants.COLUMN_LENGTH_BASE_CODE)
    private String code;

    @Column(name = "date_start", columnDefinition = Constants.COLUMN_DEFINE_DATE, nullable = false)
    private LocalDate dateStart;

    @Column(name = "date_end", columnDefinition = Constants.COLUMN_DEFINE_DATE, nullable = false)
    private LocalDate dateEnd;

    @Column(name = "duration", nullable = false)
    private int duration;   // 주말은 제외하고 계산된다.

    // 연차 사용 카운트, 반차인 경우 0.5 계산을 위해서 float을 적용해야 하나 MSSQL의 float이 java double과 매칭되므로 double로 처리한다.
    @Column(name = "count")
    private double count;


    @Override
    public Dayoff insertDocs(Document docs) {
        this.docs = docs;
        return updateDuration();
    }
    private Dayoff updateDuration() {
        // 주말이 포함된 경우 기간에서 제외하고 카운트 한다.
        if(this.dateStart != null && this.dateEnd != null) {
            int dtDuration = (int) ChronoUnit.DAYS.between(this.dateStart, this.dateEnd) + 1;  // 시작날짜도 포함하기 위해서 1 더함. 시작일, 종료일이 같은 날이면 1이 된다.
            int weekCount = 0;
            LocalDate date = this.dateStart;
            
            for(int i=0; i<dtDuration; i++) {
                if(date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7) {  // 월요일 1 ~ 일요일 7, 토요일이나 일요일이면 weekCount 증가
                    weekCount++;
                }
                date = date.plusDays(1);   // 다음번 날짜로 이동해서 계속 주말 체크
            }
            this.duration = dtDuration - weekCount;
            this.count = this.duration;

            // 반차 코드인 경우
            if(this.code != null && (this.code.equals("110") || this.code.equals("120")))
                this.count /= 2;
        }
        return this;
    }
}
