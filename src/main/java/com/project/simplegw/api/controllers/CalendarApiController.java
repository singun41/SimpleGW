package com.project.simplegw.api.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.api.dtos.send.DtosHoliday;
import com.project.simplegw.api.services.CalendarApiService;
import com.project.simplegw.system.helpers.ResponseConverter;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
public class CalendarApiController {
    private final CalendarApiService service;

    @Autowired
    public CalendarApiController(CalendarApiService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    @GetMapping("/holidays/{year}/{month}")
    public ResponseEntity<Object> getHolidays(@PathVariable int year, @PathVariable int month) {
        List<DtosHoliday> holidays = new ArrayList<>();

        LocalDate date = LocalDate.of(year, month, 1);
        LocalDate previousMonth = date.minusMonths(1L);
        LocalDate nextMonth = date.plusMonths(1L);

        holidays.addAll( service.getHolidays(previousMonth.getYear(), previousMonth.getMonthValue()) );
        holidays.addAll( service.getHolidays(year, month) );
        holidays.addAll( service.getHolidays(nextMonth.getYear(), nextMonth.getMonthValue()) );
        holidays.forEach(e -> e.setIndex(holidays.indexOf(e)));

        return ResponseConverter.ok( holidays );
    }

    

    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @GetMapping("/manual")
    public ResponseEntity<Object> cacheManualRemove(@AuthenticationPrincipal LoginUser loginUser) {
        if( Role.ADMIN != loginUser.getMember().getRole() )
            return ResponseConverter.unauthorized();
            
        service.cacheManualRemove(loginUser);
        return ResponseConverter.ok();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
