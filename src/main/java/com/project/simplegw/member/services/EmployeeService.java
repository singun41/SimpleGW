package com.project.simplegw.member.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.simplegw.member.dtos.send.DtosEmployeesProfile;
import com.project.simplegw.member.helpers.MemberConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeService {
    private final MemberService service;
    private final MemberConverter converter;
    private final MemberPortraitService portraitService;

    @Autowired
    public EmployeeService(MemberService serevice, MemberConverter converter, MemberPortraitService portraitService) {
        this.service = serevice;
        this.converter = converter;
        this.portraitService = portraitService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }





    public List<DtosEmployeesProfile> getEmployeesProfile(String team) {
        return service.getTeamMembers(team).stream().map(
            e -> converter.getEmployeesProfile( service.getProfile(e.getId()).calcDuration() ).setPortrait( portraitService.getPortrait(e.getId()) )
        ).collect(Collectors.toList());
    }
}
