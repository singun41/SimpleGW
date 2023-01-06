package com.project.simplegw.system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.system.dtos.receive.DtorMenuAuthority;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auths")
public class MenuAuthorityController {
    private final MenuAuthorityService service;

    @Autowired
    public MenuAuthorityController(MenuAuthorityService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping("/{menu}")
    public ResponseEntity<Object> getAuths(@PathVariable String menu, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();
            
        return ResponseConverter.ok( service.getAuths(Menu.valueOf(menu.toUpperCase())) );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody DtorMenuAuthority dto, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        return ResponseConverter.message( service.update(id, dto), ResponseMsg.UPDATED  );
    }
}
