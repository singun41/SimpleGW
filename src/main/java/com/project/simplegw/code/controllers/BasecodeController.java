package com.project.simplegw.code.controllers;

import com.project.simplegw.code.dtos.receive.DtorBasecode;
import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/basecode")
public class BasecodeController {
    private final BasecodeService service;

    @Autowired
    public BasecodeController(BasecodeService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping("/{type}")
    public ResponseEntity<Object> getCodes(@PathVariable String type, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        return ResponseConverter.ok(service.getCodes(BasecodeType.valueOf(type.toUpperCase())));
    }


    @PostMapping("/{type}")
    public ResponseEntity<Object> create(@PathVariable String type, @Validated @RequestBody DtorBasecode dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.create(BasecodeType.valueOf(type), dto) , ResponseMsg.INSERTED
        );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Validated @RequestBody DtorBasecode dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();

        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message(
            service.update(id, dto) , ResponseMsg.UPDATED
        );
    }
}
