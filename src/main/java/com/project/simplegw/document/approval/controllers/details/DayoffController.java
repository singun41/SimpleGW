package com.project.simplegw.document.approval.controllers.details;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorDayoff;
import com.project.simplegw.document.approval.dtos.receive.details.dayoff.DtorTempDayoff;
import com.project.simplegw.document.approval.services.details.DayoffService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ResponseMsg;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequestMapping("/approval/dayoff")
public class DayoffController {
    private final DayoffService service;

    @Autowired
    public DayoffController(DayoffService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    

    @GetMapping("/details/{docsId}")
    public ResponseEntity<Object> getDetails(@PathVariable Long docsId) {   // 문서 수정시 필요한 디테일 데이터를 별도로 전달하기 위함.
        return ResponseConverter.ok( service.getDetails(docsId) );
    }



    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody DtorDayoff dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);

        return ResponseConverter.message( service.create(dto, loginUser), ResponseMsg.INSERTED );
    }
    
    
    @PatchMapping("/{docsId}")
    public ResponseEntity<Object> update(@PathVariable Long docsId, @Validated @RequestBody DtorDayoff dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message( service.update(docsId, dto, loginUser), ResponseMsg.UPDATED );
    }





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/details/{docsId}/temp")
    public ResponseEntity<Object> getTempDetails(@PathVariable Long docsId) {
        return ResponseConverter.ok( service.getTempDetails(docsId) );
    }
    
    @PostMapping("/temp")
    public ResponseEntity<Object> createTemp(@Validated @RequestBody DtorTempDayoff dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message( service.createTemp(dto, loginUser), ResponseMsg.INSERTED );
    }

    @PatchMapping("/temp/{docsId}")
    public ResponseEntity<Object> updateTemp(@PathVariable Long docsId, @Validated @RequestBody DtorTempDayoff dto, BindingResult result, @AuthenticationPrincipal LoginUser loginUser) {
        if(result.hasErrors())
            return ResponseConverter.badRequest(result);
        
        return ResponseConverter.message( service.updateTemp(docsId, dto, loginUser), ResponseMsg.UPDATED );
    }

    @DeleteMapping("/temp/{docsId}")
    public ResponseEntity<Object> deleteTemp(@PathVariable Long docsId, @AuthenticationPrincipal LoginUser loginUser) {
        return ResponseConverter.message( service.deleteTemp(docsId, loginUser), ResponseMsg.DELETED );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- temp docs ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
