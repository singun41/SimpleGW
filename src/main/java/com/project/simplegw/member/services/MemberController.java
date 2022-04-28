package com.project.simplegw.member.services;

import java.util.List;

import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.member.dtos.MemberDTO;
import com.project.simplegw.member.dtos.MemberDTOforAdmin;
import com.project.simplegw.member.dtos.MemberInfoDTO;
import com.project.simplegw.member.dtos.PasswordDTO;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(path = "/team-member", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<MemberDTO> getTeamMembers(@RequestParam String team) {
        return memberService.getTeamMembers(team);
    }

    @PutMapping("/my-info")   // update --> PutMapping
    public ResponseEntity<Object> updateMyInfo(@AuthenticationPrincipal SecurityUser loginUser, @RequestBody MemberInfoDTO dto) {
        dto.setId(loginUser.getMember().getId());
        return ResponseEntityConverter.getFromRequestResult(memberService.updateMyInfo(dto));
    }

    @PutMapping("/my-pw")
    public ResponseEntity<Object> updateMyPw(@AuthenticationPrincipal SecurityUser loginUser, @RequestBody PasswordDTO dto) {
        dto.setId(loginUser.getMember().getId());
        return ResponseEntityConverter.getFromRequestResult(memberService.updateMyPw(dto));
    }

	@GetMapping(path = "/members-info", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public List<MemberInfoDTO> memberInfoListPage(@RequestParam String team) {
		return memberService.getMemberInfoList(team);
	}

    @GetMapping(path = "/admin/member-list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<MemberDTOforAdmin> getAllMember(@AuthenticationPrincipal SecurityUser loginUser, @RequestParam boolean isRetired) {
        if(loginUser.getMember().getRole().equals(MemberRole.ADMIN)) {
            return memberService.getAllMember(isRetired);
        } else {
            return null;
        }
    }

    @PostMapping(path = "/admin/member")
    public ResponseEntity<Object> saveMemberForAdmin(@RequestBody MemberDTOforAdmin dto) {
        RequestResult result = memberService.saveMemberforAdmin(dto);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
}
