package com.project.simplegw.work.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;
import com.project.simplegw.work.dtos.WorkRecordDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkRecordController {
    
    private final WorkRecordService workRecordService;

    @Autowired
    public WorkRecordController(WorkRecordService workRecordService) {
        this.workRecordService = workRecordService;
    }

    @GetMapping(path = "/work-record", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<WorkRecordDTO> getWorkRecord(@RequestParam("searchDate") @DateTimeFormat(iso = ISO.DATE) LocalDate searchDate, @AuthenticationPrincipal SecurityUser loginUser) {
        return workRecordService.searchWorkRecordByWorkDate(searchDate, loginUser.getMember().getId());
    }

    @PutMapping(path = "/work-record")
    public ResponseEntity<Object> saveWorkRecord(@RequestBody WorkRecordDTO dto, @AuthenticationPrincipal SecurityUser loginUser) {
        return ResponseEntityConverter.getFromRequestResult(workRecordService.saveWorkRecord(dto, loginUser.getMember().getId()));
    }

    @GetMapping(path = "/member-work-record-list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<WorkRecordDTO> getWorkRecordListForTeamLeader(@RequestParam("searchDate") @DateTimeFormat(iso = ISO.DATE) LocalDate searchDate, @AuthenticationPrincipal SecurityUser loginUser) {
        if(loginUser.getMember().getRole().equals(MemberRole.USER) || loginUser.getMember().getRole().equals(MemberRole.MANAGER)) {
            return new ArrayList<WorkRecordDTO>();
        } else {
            return workRecordService.searchAllTeamMemberWorkRecordForTeamLeader(searchDate, loginUser.getMember().getId());
        }
    }

    @GetMapping(path = "/team-work-record-list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<WorkRecordDTO> getWorkRecordList(@RequestParam("searchDate") @DateTimeFormat(iso = ISO.DATE) LocalDate searchDate, @RequestParam("team") String team, @AuthenticationPrincipal SecurityUser loginUser) {
        if(loginUser.getMember().getRole().equals(MemberRole.USER) || loginUser.getMember().getRole().equals(MemberRole.MANAGER) || loginUser.getMember().getRole().equals(MemberRole.LEADER)) {
            return new ArrayList<WorkRecordDTO>();
        } else {
            return workRecordService.searchAllWorkRecordByWorkDateAndTeam(searchDate, team);
        }
    }
}
