package com.project.simplegw.schedule.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.code.dtos.send.DtosBasecode;
import com.project.simplegw.code.services.BasecodeService;
import com.project.simplegw.code.vos.BasecodeType;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.dtos.admin.receive.DtorColor;
import com.project.simplegw.schedule.dtos.admin.send.DtosColor;
import com.project.simplegw.schedule.dtos.receive.DtorSchedule;
import com.project.simplegw.schedule.dtos.receive.DtorScheduleIncludedTime;
import com.project.simplegw.schedule.dtos.send.DtosSchedule;
import com.project.simplegw.schedule.dtos.send.DtosScheduleMin;
import com.project.simplegw.schedule.entities.Schedule;
import com.project.simplegw.schedule.helpers.ScheduleConverter;
import com.project.simplegw.schedule.repositories.ScheduleRepo;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.schedule.vos.SearchOption;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.MenuAuthorityService;
import com.project.simplegw.system.vos.Menu;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ScheduleService {
    private final Menu SCHEDULE = Menu.SCHEDULE;

    private final ScheduleRepo repo;
    private final ScheduleConverter converter;
    private final ColorService colorService;
    private final BasecodeService basecodeService;
    private final MemberService memberService;
    private final MenuAuthorityService authService;
    private final ScheduleCountCacheService countCacheService;
    
    @Autowired
    public ScheduleService(
        ScheduleRepo repo, ScheduleConverter converter, ColorService colorService, BasecodeService basecodeService,
        MemberService memberService, MenuAuthorityService authService, ScheduleCountCacheService countCacheService
    ) {
        this.repo = repo;
        this.converter = converter;
        this.colorService = colorService;
        this.basecodeService = basecodeService;
        this.memberService = memberService;
        this.authService = authService;
        this.countCacheService = countCacheService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public List<DtosColor> getColorList(ScheduleType type) {
        List<DtosColor> colorList = colorService.getList(type);
        
        try {
            // 기초코드의 value값을 업데이트해서 리턴한다.
            Map<String, String> codeMap = basecodeService.getCodes( BasecodeType.valueOf(type.name()) ).stream().collect(Collectors.toMap(DtosBasecode::getCode, DtosBasecode::getValue));
            colorList.forEach(color -> color.setValue( codeMap.get(color.getCode()) ));

        } catch(Exception e) {
            log.warn("ScheduleType parameter not exists in basecode tables. type: {}", type.name());
        }

        return colorList;
    }

    public ServiceMsg updateColor(ScheduleType type, List<DtorColor> dtos) {
        return colorService.update(type, dtos);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //



    private Map<String, String> getCodeMap(ScheduleType type) {
        return basecodeService.getCodes(BasecodeType.valueOf(type.name())).stream().collect(Collectors.toMap(DtosBasecode::getCode, DtosBasecode::getValue));
    }


    public DtosSchedule getSchedule(Long id) {
        Schedule entity = repo.findById(id).get();
        Map<String, String> codeMap = getCodeMap( entity.getType() );

        return (DtosSchedule) converter.getDtosSchedule(entity).setValue( codeMap.get(entity.getCode()) );
    }


    private List<Schedule> getEntities(ScheduleType type, LocalDate from, LocalDate to) {
        // 기간으로만 조회해서 스트림에서 필터링한다.
        // 기간만 사용하게 되면 DB 서버의 버퍼 캐시 히트율도 올라간다.
        return repo.findByDateFromBetweenOrderById(from, to).stream().filter(e -> e.getType() == type).collect(Collectors.toList());
    }

    private List<DtosScheduleMin> getScheduleList(ScheduleType type, LocalDate from, LocalDate to, LoginUser loginUser) {
        Map<String, String> colorMap = colorService.getList(type).stream().collect(Collectors.toMap(DtosColor::getCode, DtosColor::getHexValue));
        Map<String, String> codeMap = getCodeMap(type);

        return getEntities(type, from, to).stream().map(e ->
                converter.getDtosScheduleMin(e)
                    .setValue( codeMap.get(e.getCode()) )
                    .setColorHex( colorMap.get(e.getCode()) )
                    .setMine( e.getMemberId().equals(loginUser.getMember().getId()) ))
            .sorted(Comparator.comparing(DtosScheduleMin::getDateFrom).thenComparing(DtosScheduleMin::getCode))
            .collect(Collectors.toList());
    }

    
    // 일정은 캐시에서 제외한다. 일정 등록이 빈번하고, SearchOption마다 캐시 종류가 여러개로 나뉘기 때문에 효율이 좋지 못함.
    public List<DtosScheduleMin> getScheduleList(ScheduleType type, SearchOption option, int year, int month, LoginUser loginUser) {
        Long userId = loginUser.getMember().getId();
        log.info("getScheduleList(..) method called. user: {}", userId);

        // full-calendar에 렌더링할 때 현재월의 앞뒤로 최대 2주 정도 날짜가 표시되기 때문에 붙여준다.
        LocalDate from = LocalDate.of(year, month, 1).minusWeeks(2L);
        LocalDate to = YearMonth.from(LocalDate.of(year, month, 1)).atEndOfMonth().plusWeeks(2L);
        
        List<DtosScheduleMin> list = null;

        switch(option) {
            case MINE -> { list = getScheduleList(type, from, to, loginUser).stream().filter(e -> e.getMemberId().equals(loginUser.getMember().getId())).collect(Collectors.toList()); }

            case TEAM -> {
                String myTeam = memberService.getMemberData(loginUser).getTeam();
                list = getScheduleList(type, from, to, loginUser).stream()
                    .filter(e ->
                        e.getMemberId().equals(loginUser.getMember().getId()) || e.getTeam().equals( myTeam )
                    )
                    .collect(Collectors.toList());
            }

            case AROUND_10_DAYS -> {
                // 이 옵션의 경우 현재월 데이터만 리턴한다.
                if(month == LocalDate.now().getMonthValue()) {
                    from = LocalDate.now().minusDays(10L);
                    to = LocalDate.now().plusDays(10L);

                    list = getScheduleList(type, from, to, loginUser);
                }
            }

            case ALL -> { list = getScheduleList(type, from, to, loginUser); }

            default -> {}
        }

        log.info("user: {}, type: {}, option: {}, from: {}, to: {}", userId, type.name(), option.name(), from.toString(), to.toString());
        return list == null ? new ArrayList<DtosScheduleMin>() : list;
    }



    ServiceMsg save(ScheduleType type, DtorSchedule dto, LoginUser loginUser) {
        if( ! authService.isWritable(SCHEDULE, loginUser) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );


        if(dto.getDateFrom().isAfter(dto.getDateTo()))
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("시작 날짜가 종료 날짜보다 이후입니다.");

        try {
            Schedule savedEntity;

            if(dto instanceof DtorScheduleIncludedTime dtoIncludedTime) {
                // From, To 가 같을 때만 시간 순서를 검증한다.
                if( dtoIncludedTime.getDateTo().equals(dtoIncludedTime.getDateFrom()) && dtoIncludedTime.getTimeFrom().isAfter(dtoIncludedTime.getTimeTo()) )
                    return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("시작 시간이 종료 시간보다 이후입니다.");
                        
                savedEntity = repo.save( converter.getEntity(dtoIncludedTime).setMemberData(memberService.getMemberData(loginUser)).updateType(type) );

            } else {
                savedEntity = repo.save( converter.getEntity(dto).setMemberData(memberService.getMemberData(loginUser)).updateType(type) );
            }

            return new ServiceMsg().setResult(ServiceResult.SUCCESS).setReturnObj(savedEntity.getId());

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("save exception. parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("일정 저장 에러입니다. 관리자에게 문의하세요.");
        }
    }



    ServiceMsg update(ScheduleType type, Long id, DtorSchedule dto, LoginUser loginUser) {
        Schedule entity = repo.findById(id).orElseGet(Schedule::new);

        if(entity.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("업데이트할 일정 데이터가 존재하지 않습니다. 관리자에게 문의하세요.");

        if( ! authService.isUpdatable(SCHEDULE, loginUser, entity.getMemberId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );

        if(entity.getType() != type)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("업데이트 대상이 '").append(type.getTitle()).append("' 이 아닙니다.").toString());

        if(entity.getDateFrom().isBefore(LocalDate.now()))
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("지난 일정 데이터는 수정할 수 없습니다.").toString());

        if( ! (entity.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || entity.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("수정할 수 있는 일정 코드가 아닙니다.").toString());

        try {
            repo.save( entity.updateData(dto) );
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update exception. parameters: {}, {}, {}, user: {}", type.name(), id, dto.toString(), loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("일정 데이터 업데이트 에러입니다. 관리자에게 문의하세요.");
        }
    }



    ServiceMsg delete(ScheduleType type, Long id, LoginUser loginUser) {
        Schedule entity = repo.findById(id).orElseGet(Schedule::new);

        if(entity.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("삭제할 일정 데이터가 존재하지 않습니다.");
        
        if( ! authService.isDeletable(SCHEDULE, loginUser, entity.getMemberId()) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg( ResponseMsg.UNAUTHORIZED.getTitle() );
        
        if(entity.getType() != type)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("삭제 대상이 '").append(type.getTitle()).append("' 이 아닙니다.").toString());
        
        if(entity.getDateFrom().isBefore(LocalDate.now()))
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("지난 일정 데이터는 삭제할 수 없습니다.").toString());

        if( ! (entity.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode()) || entity.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS_DIRECT.getCode())) )
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg(new StringBuilder("삭제할 수 있는 일정 코드가 아닙니다.").toString());

        try {
            repo.delete(entity);
            
            if(type == ScheduleType.PERSONAL && entity.getDateFrom().equals(LocalDate.now()))
                countCacheService.clear();
            
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("delete exception. parameters: {}, {}, {}, user: {}", type.name(), id, loginUser.getMember().getId());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("일정 데이터 삭제 에러입니다. 관리자에게 문의하세요.");
        }
    }





    List<Schedule> saveRelatedApprovalDocs(List<DtorSchedule> dtos, Long memberId) {   // 근태와 관련된 결재문서 최종 승인 시 스케줄 자동 등록.
        try {
            List<Schedule> list = dtos.stream().map(e -> converter.getEntity(e).setMemberData(memberService.getMemberData(memberId)).updateType(ScheduleType.PERSONAL)).collect(Collectors.toList());
            return repo.saveAll(list);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("saveForDayoff exception.");
            log.warn("parameters: {}, user: {}", dtos.toString(), memberId);

            return null;
        }
    }
}
