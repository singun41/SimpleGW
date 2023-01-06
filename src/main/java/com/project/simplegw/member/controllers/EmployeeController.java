package com.project.simplegw.member.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.simplegw.member.services.EmployeeService;
import com.project.simplegw.system.helpers.ResponseConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    @GetMapping("/{team}")
    public ResponseEntity<Object> getEmployeesProfile(@PathVariable String team) {
        return ResponseConverter.ok( service.getEmployeesProfile(team) );
    }
}
