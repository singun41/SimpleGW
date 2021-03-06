package com.project.simplegw.work.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.work.dtos.WorkRecordDTO;
import com.project.simplegw.work.entities.WorkRecord;
import com.project.simplegw.work.repositories.WorkRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class WorkRecordService {

    private final MemberService memberService;
    private final WorkRecordRepository workRecordRepo;
    private final WorkRecordConverter workRecordConverter;

    @Autowired
    public WorkRecordService(MemberService memberService, WorkRecordRepository workRecordRepo, WorkRecordConverter workRecordConverter) {
        this.memberService = memberService;
        this.workRecordRepo = workRecordRepo;
        this.workRecordConverter = workRecordConverter;
    }

    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //
    private WorkRecordDTO convertEntityToDto(WorkRecord entity) {
        return workRecordConverter.getDTO(entity);
    }
    private WorkRecord convertDtoToEntity(WorkRecordDTO dto) {
        return workRecordConverter.getEntity(dto);
    }
    private List<WorkRecordDTO> convertEntityListToDtoList(List<WorkRecord> entities) {
        List<WorkRecordDTO> dtoList = new ArrayList<>();
        entities.forEach(entity -> dtoList.add(convertEntityToDto(entity)));
        return dtoList;
    }
    // ----- ----- ----- ----- ----- Converting ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    public List<WorkRecordDTO> searchWorkRecordByWorkDate(LocalDate workDate, Long memberId) {
        List<WorkRecordDTO> workRecordList = new ArrayList<>();

        WorkRecord before = workRecordRepo.getByWorkDateAndMemberDetailsId(workDate.minusDays(1L), memberId);
        WorkRecord now = workRecordRepo.getByWorkDateAndMemberDetailsId(workDate, memberId);

        workRecordList.add(convertEntityToDto(before == null ? new WorkRecord() : before));
        workRecordList.add(convertEntityToDto(now == null ? new WorkRecord() : now));
        
        return workRecordList;
    }
    public List<WorkRecordDTO> searchAllTeamMemberWorkRecordForTeamLeader(LocalDate workDate, Long leaderMemberId) {   // ?????? ???????????? ?????? ???????????? ??????
        String team = memberService.searchMemberDetailsById(leaderMemberId).getTeam();
        return searchAllWorkRecordByWorkDateAndTeam(workDate, team);
    }
    public List<WorkRecordDTO> searchAllWorkRecordByWorkDateAndTeam(LocalDate workDate, String team) {   // ?????? ???????????? ??????
        if(team == null || team.equals("")) {
            return convertEntityListToDtoList(workRecordRepo.findAllByWorkDateOrderByTeamAscNameAsc(workDate));
        } else {
            return convertEntityListToDtoList(workRecordRepo.findAllByWorkDateAndTeamOrderByName(workDate, team));
        }
    }
    public List<WorkRecordDTO> searchAllWorkRecordByWorkDate(LocalDate workDate) {   // ?????? ?????? ?????? ??????
        return convertEntityListToDtoList(workRecordRepo.findAllByWorkDateOrderByTeamAscNameAsc(workDate));
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- insert and update ----- ----- ----- ----- ----- //
    public RequestResult saveWorkRecord(WorkRecordDTO dto, Long memberId) {
        if(dto.getWorkDate().isAfter(LocalDate.now())) {
            return RequestResult.getDefaultFail("???????????? ?????????????????????." + System.lineSeparator() + "?????? ??????????????? ????????? ???????????????.");
        }

        if(dto.getTodayWork() == null || dto.getTodayWork().isBlank()) {
            return RequestResult.getDefaultFail("?????? ?????? ????????? ???????????????.");
        }

        dto.setTodayWork(dto.getTodayWork().strip());
        if(dto.getNextWorkPlan() != null) {
            dto.setNextWorkPlan(dto.getNextWorkPlan().strip());
        }

        MemberDetails member = memberService.searchMemberDetailsById(memberId);
        WorkRecord entity = workRecordRepo.getByWorkDateAndMemberDetailsId(dto.getWorkDate(), member.getId());

        if(entity == null) {   // insert
            entity = convertDtoToEntity(dto);
            workRecordRepo.save(entity.insertMemberDetails(member));
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_INSERTED);

        } else {   // update
            entity.updateTodayWork(dto.getTodayWork()).updateNextWorkPlan(dto.getNextWorkPlan());
            workRecordRepo.save(entity);
            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_UPDATED);
        }
    }
    // ----- ----- ----- ----- ----- insert and update ----- ----- ----- ----- ----- //
}
