package com.project.simplegw.schedule.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.document.approval.repositories.details.dayoff.DayoffRepo;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.member.services.MemberAddOnService;
import com.project.simplegw.schedule.dtos.receive.DtorSchedule;
import com.project.simplegw.schedule.entities.Schedule;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PersonalScheduleService {
    private final ScheduleType PERSONAL = ScheduleType.PERSONAL;
    private final ScheduleService service;
    private final ScheduleCountService countService;
    
    private final DayoffRepo dayoffRepo;   // 순환참조 때문에 repository를 직접 가져온다.
    private final BasecodeService basecodeService;

    // 휴가신청서 최종 승인시 연차 카운트 업데이트 작업을 위해 추가.
    private final MemberAddOnService memberAddOnService;

    @Autowired
    public PersonalScheduleService(ScheduleService service, ScheduleCountService countService, DayoffRepo dayoffRepo, BasecodeService basecodeService, MemberAddOnService memberAddOnService) {
        this.service = service;
        this.countService = countService;
        this.dayoffRepo = dayoffRepo;
        this.basecodeService = basecodeService;
        this.memberAddOnService = memberAddOnService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    public ServiceMsg save(DtorSchedule dto, LoginUser loginUser) {
        ServiceMsg result = service.save(PERSONAL, dto, loginUser);
        
        if(result.getResult() == ServiceResult.SUCCESS)
            countService.create((Long) result.getReturnObj());

        return result;
    }


    public ServiceMsg update(Long id, DtorSchedule dto, LoginUser loginUser) {
        ServiceMsg result = service.update(PERSONAL, id, dto, loginUser);

        if(result.getResult() == ServiceResult.SUCCESS)
            countService.update(id, loginUser);

        return result;
    }

    
    public ServiceMsg delete(Long id, LoginUser loginUser) {
        return service.delete(PERSONAL, id, loginUser);
    }






    @Async   // 휴가신청서 등 근태관련 결재문서 최종 승인시 이 메서드를 호출하여 스케줄 등록. ApproverService에서 호출.
    public void relatedApprovalDocs(Docs docs) {
        switch(docs.getType()) {
            case DAYOFF -> createDayoffSchedule(docs);

            default -> {}
        }
    }



    // 휴가 신청서 최종 승인시 스케줄 자동 등록 기능
    private void createDayoffSchedule(Docs docs) {
        Map<String, String> codeMap = basecodeService.getCodes(BasecodeType.DAYOFF).stream().collect(Collectors.toMap(DtosBasecode::getCode, DtosBasecode::getValue));

        List<DtorSchedule> dtos = dayoffRepo.findByDocsId(docs.getId()).stream()
            .filter(e ->
                ! ( // 휴가신청서 코드 중 제외할 리스트
                    e.getCode().equals("200") ||   // 단축근무일
                    e.getCode().equals("300") || e.getCode().equals("301") ||   // 임신 12주, 36주 단축근무
                    e.getCode().equals("400") || e.getCode().equals("401")   // 조퇴, 외출
                )
            ).map(e ->
                new DtorSchedule()
                    .setCode(
                        e.getCode().equals("101") ? e.getCode() :
                        e.getCode().equals("102") ? e.getCode() : "100"   // 반차(오전), 반차(오후)가 아닌 나머지는 모두 휴가로 처리.
                    )
                    .setDateFrom(e.getDateFrom()).setDateTo(e.getDateTo())
                    .setTitle(
                        new StringBuilder("휴가 코드: ").append(codeMap.get(e.getCode())).toString()
                    )
                    .setContent(
                        new StringBuilder("시스템에서 등록한 일정입니다.").append("\n").append(docs.getType().getTitle()).append(" [ ").append(docs.getId()).append(" ]").toString()
                    )
            ).collect(Collectors.toList());

        List<Schedule> savedEntities = service.saveRelatedApprovalDocs(dtos, docs.getWriterId());
        if(savedEntities != null )
            savedEntities.forEach(entity -> countService.create(entity));
        
        memberAddOnService.updateMemberDayoffCount(docs);   // 휴가 신청서 연차 카운트 계산.
    }
}
