package com.project.simplegw.member.helpers;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberCreate;
import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.member.dtos.admin.send.DtosMemberDetails;
import com.project.simplegw.member.dtos.send.DtosEmployeesProfile;
import com.project.simplegw.member.dtos.send.DtosEnvSetting;
import com.project.simplegw.member.dtos.send.DtosProfile;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.entities.MemberEnvSetting;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberConverter {
    @Mapping(target = "userId", source = "id")   // dto의 id가 entity의 userId로 바인딩.
    @Mapping(target = "password", ignore = true)   // MemberService에서 암호화해서 넘긴다.
    Member getMember(DtorMemberCreate dto);

    MemberDetails getDetails(DtorMemberCreate dto);

    DtosMember getDtosMember(Member entity);
    DtosMember getDtosMember(MemberData memberData);

    DtosProfile getDtosProfile(MemberDetails entity);
    DtosMemberDetails getDtosMemberDetails(MemberDetails entity);

    MemberData getMemberData(MemberDetails entity);

    DtosEmployeesProfile getEmployeesProfile(DtosProfile dto);

    DtosEnvSetting getDtosEnvSetting(MemberEnvSetting entity);
}
