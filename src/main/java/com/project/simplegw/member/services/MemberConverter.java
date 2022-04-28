package com.project.simplegw.member.services;

import com.project.simplegw.member.dtos.MemberDTO;
import com.project.simplegw.member.dtos.MemberDTOforAdmin;
import com.project.simplegw.member.dtos.MemberInfoDTO;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.entities.MemberDetails;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE) // 이게 없으면 bean 등록이 안 되므로 반드시 작성.
public interface MemberConverter {
    // MemberMapStructMapper INSTANCE = Mappers.getMapper(MemberMapStructMapper.class); // bean으로 등록해서 사용할 때에는 이 코드는 필요없음.

    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //
    
    // Member Entity --> LoginDTO
    // 바꿀 이유가 없음.
    // LoginDTO getLoginDTO(Member member);

    // MemberDetails Entity --> MemberDTO
    
    MemberDTO getMemberDTO(MemberDetails details);  // 유저관리 페이지나 같은 팀 멤버를 찾을 때 필요

    @Mapping(source = "nameEng", target = "nameEng", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "mobileNo", target = "mobileNo", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "mailAddress", target = "mailAddress", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "tel", target = "tel", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    MemberInfoDTO getDto(MemberDetails details);

    // ----- ----- ----- ----- ----- Entity to DTO ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
    
    // LoginDTO --> Member Entity
    // LoginDTO에서 Member entity로 변환할 필요가 없다. 의미없는 변환이다.
    // LoginDTO에서 받아온 userId로 memberRepository의 findByUserId를 실행하면 member 객체를 찾을 수 있기 때문이다.
    // @Mapping(source = "id", target = "userId")
    // @Mapping(source = "pw", target = "password")
    // Member getMember(LoginDTO loginDTO);

    // MemberDTO --> MemberDetails Entity
    // 신규 멤버 등록시 필요
    MemberDetails getEntity(MemberDTO memberDTO);

    @Mapping(source = "userPw", target = "password")
    Member getMember(MemberDTOforAdmin dto);

    MemberDetails getEntity(MemberDTOforAdmin dto);

    // ----- ----- ----- ----- ----- DTO to Entity ----- ----- ----- ----- ----- //
}
