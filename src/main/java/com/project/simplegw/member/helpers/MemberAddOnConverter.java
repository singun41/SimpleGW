package com.project.simplegw.member.helpers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.simplegw.member.dtos.admin.send.DtosMemberAddOn;
import com.project.simplegw.member.dtos.send.DtosDayoffCnt;
import com.project.simplegw.member.entities.MemberAddOn;

@Mapper(componentModel = "spring")
public interface MemberAddOnConverter {
    @Mapping(target = "qty", source = "dayoffQty")
    @Mapping(target = "use", source = "dayoffUse")
    DtosDayoffCnt getDayoffCnt(MemberAddOn entity);

    DtosMemberAddOn getDtosmemberAddOn(MemberAddOn entity);
}
