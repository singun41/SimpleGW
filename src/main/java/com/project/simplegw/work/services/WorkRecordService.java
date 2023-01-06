package com.project.simplegw.work.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.work.dtos.receive.DtorWorkRecord;
import com.project.simplegw.work.dtos.send.DtosWorkRecordForList;
import com.project.simplegw.work.dtos.send.DtosWorkRecordPersonal;
import com.project.simplegw.work.entities.WorkRecord;
import com.project.simplegw.work.helpers.WorkRecordConverter;
import com.project.simplegw.work.repositories.WorkRecordRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class WorkRecordService {
    private final WorkRecordRepo repo;
    private final WorkRecordConverter converter;
    private final MemberService memberService;

    @Autowired
    public WorkRecordService(WorkRecordRepo repo, WorkRecordConverter converter, MemberService memberService) {
        this.repo = repo;
        this.converter = converter;
        this.memberService = memberService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    public List<String> getTeams() {
        return memberService.getTeams();
    }

    private List<WorkRecord> getEntities(LocalDate workDate) {
        return repo.findByWorkDate(workDate);
    }



    public List<DtosWorkRecordPersonal> getMyWorkRecord(LocalDate workDate, LoginUser loginUser) {
        List<WorkRecord> workRecordList = new ArrayList<>();
        Long memberId = loginUser.getMember().getId();

        workRecordList.add(
            repo.findTop1ByWorkDateLessThanAndMemberIdOrderByWorkDateDesc(workDate, memberId).orElseGet( () -> WorkRecord.builder().workDate(workDate.minusDays(1L)).build() )
        );

        workRecordList.add(
            repo.findByWorkDateAndMemberId(workDate, memberId).orElseGet( () -> WorkRecord.builder().workDate(workDate).build() )
        );

        return workRecordList.stream().map(converter::getDtosWorkRecordPersonal).collect(Collectors.toList());
    }



    public List<DtosWorkRecordForList> getMyTeamWorkRecordList(LocalDate workDate, LoginUser loginUser) {
        return getWorkRecordList(workDate, memberService.getMemberData(loginUser).getTeam());
    }

    public List<DtosWorkRecordForList> getWorkRecordList(LocalDate workDate, String team) {
        return getEntities(workDate).stream().sorted(
            Comparator.comparing(WorkRecord::getTeam).thenComparing(WorkRecord::getName)
        ).filter(
            e -> team.toLowerCase().equals("all") ? true : e.getTeam().equals(team)
        ).map(converter::getDtosWorkRecordForList).collect(Collectors.toList());
    }



    public ServiceMsg save(DtorWorkRecord dto, LoginUser loginUser) {
        Long memberId = loginUser.getMember().getId();
        Optional<WorkRecord> target = repo.findByWorkDateAndMemberId(LocalDate.parse(dto.getWorkDate()) , memberId);

        MemberData memberData = memberService.getMemberData(loginUser);
        WorkRecord entity;

        try {
            if(target.isPresent()) {
                entity = target.get();
                entity.setMemberData(memberData).updateTodayWork(dto.getTodayWork()).updateNextPlan(dto.getNextPlan());
    
            } else {
                entity = converter.getEntity(dto).setMemberData(memberData).updateWorkDate(dto.getWorkDate());
            }
    
            repo.save(entity);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
        } catch(Exception e) {
            e.printStackTrace();
            log.warn("save exception");
            log.warn("user: {}, parameters: {}", memberData.toString(), dto.toString());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("업무일지 저장 오류입니다. 관리자에게 문의하세요.");
        }
    }
}
