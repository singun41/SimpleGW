package com.project.simplegw.common.services;

import java.util.List;

import com.project.simplegw.common.dtos.CodeForAdminDTO;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class BasecodeController {
    
    private final BasecodeService basecodeService;

    @Autowired
    public BasecodeController(BasecodeService basecodeService) {
        this.basecodeService = basecodeService;
    }

    @GetMapping(path = "/admin/code-list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<CodeForAdminDTO> getCodeList(@RequestParam String type, @AuthenticationPrincipal SecurityUser loginUser) {
        if(loginUser.getMember().getRole().equals(MemberRole.ADMIN)) {
            return basecodeService.getAllCodes(type);
        } else {
            return null;
        }
    }

    @PostMapping(path = "/admin/code")
    public ResponseEntity<Object> saveCode(@RequestBody CodeForAdminDTO dto, @AuthenticationPrincipal SecurityUser loginUser) {
        if(loginUser.getMember().getRole().equals(MemberRole.ADMIN)) {
            return ResponseEntityConverter.getFromRequestResult(basecodeService.saveCode(dto));
        } else {
            return null;
        }
    }
}
