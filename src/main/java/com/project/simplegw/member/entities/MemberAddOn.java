package com.project.simplegw.member.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.simplegw.document.approval.entities.details.dayoff.Dayoff;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberAddOnUpdate;
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
@ToString(callSuper = true, exclude = "member")   // lazy loading 일 때 제외하지 않으면 no session 에러가 난다.
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // entity의 기본 생성자는 반드시 public or protected 이어야 한다.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member_add_on", indexes = @Index(columnList = "member_id"))
public class MemberAddOn extends EntitiesCommon {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(name = "updated_datetime", nullable = false, updatable = true, columnDefinition = Constants.COLUMN_DEFINE_DATETIME)
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;



    @Column(name = "dayoff_qty", nullable = false, updatable = true)
    private double dayoffQty;

    @Column(name = "dayoff_use", nullable = false, updatable = true)
    private double dayoffUse;





    public MemberAddOn resetDayoffCnt(DtorMemberAddOnUpdate dto) {
        if(dto.isUpdateDayoffQty())
            this.dayoffQty = dto.getDayoffQty();
        
        if(dto.isUpdateDayoffUse())
            this.dayoffUse = dto.getDayfoffUse();

        return this;
    }

    public MemberAddOn updateDayoffUseCnt(List<Dayoff> list) {
        list.forEach( e -> this.dayoffUse += e.getCount() );
        return this;
    }
}
