package com.project.simplegw.schedule.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.DayoffDTO;
import com.project.simplegw.common.dtos.BasecodeDTO;
import com.project.simplegw.common.services.BasecodeService;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.common.vos.SseData;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.dtos.ScheduleDTO;
import com.project.simplegw.schedule.entities.Schedule;
import com.project.simplegw.schedule.repositories.ScheduleRepository;
import com.project.simplegw.schedule.vos.ScheduleType;
import com.project.simplegw.system.services.SseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ScheduleService {
    private final ScheduleRepository scheduleRepo;
    private final ScheduleConverter scheduleConverter;
    private final MemberService memberService;
    private final BasecodeService basecodeService;
    private final SseService sseService;

    @Autowired
    public ScheduleService(
        ScheduleRepository scheduleRepo, ScheduleConverter scheduleConverter, MemberService memberService,
        BasecodeService basecodeService, SseService sseService
    ) {
        this.scheduleRepo = scheduleRepo;
        this.scheduleConverter = scheduleConverter;
        this.memberService = memberService;
        this.basecodeService = basecodeService;
        this.sseService = sseService;
    }

    public List<ScheduleType> getTypes() {
        return Arrays.asList(ScheduleType.values()).stream().collect(Collectors.toList());
    }

    public List<BasecodeDTO> getScheduleCodeListInType(ScheduleType type) {
        return basecodeService.getCodeList(BasecodeType.valueOf(type.name()));
    }

    public List<ScheduleDTO> getCompanyEvent() {
        LocalDate current = LocalDate.now();
        LocalDate nextMonth = current.plusMonths(1);
        LocalDate nextMonthFirstDay = LocalDate.of(nextMonth.getYear(), nextMonth.getMonthValue(), 1);

        List<Schedule> entities = new ArrayList<>();
        
        entities.addAll(   // 이번 달 행사
            scheduleRepo.findTop5ByTypeAndYearAndMonthAndDatetimeStartGreaterThanEqualOrderByDatetimeStart(
                ScheduleType.COMPANY, current.getYear(), current.getMonthValue(),
                LocalDateTime.of(current.getYear(), current.getMonthValue(), current.getDayOfMonth(), Integer.parseInt("0"), Integer.parseInt("0"))
            )
        );

        entities.addAll(   // 다음 달 행사
            scheduleRepo.findTop5ByTypeAndYearAndMonthAndDatetimeStartGreaterThanEqualOrderByDatetimeStart(
                ScheduleType.COMPANY, nextMonthFirstDay.getYear(), nextMonthFirstDay.getMonthValue(),
                LocalDateTime.of(nextMonthFirstDay.getYear(), nextMonthFirstDay.getMonthValue(), nextMonthFirstDay.getDayOfMonth(), Integer.parseInt("0"), Integer.parseInt("0"))
            )
        );

        return entities.stream().map(scheduleConverter::getDto).collect(Collectors.toList());
    }

    private List<Schedule> getScheduleEntities(ScheduleType type, int year, int month) {
        return scheduleRepo.findAllByTypeAndYearAndMonth(type, year, month);
    }
    private List<Schedule> getScheduleEntities(ScheduleType type, int year, int month, int startWeekNumber, int endWeekNumber) {
        return scheduleRepo.findAllByTypeAndYearAndMonthAndWeekOfYearBetween(type, year, month, startWeekNumber, endWeekNumber);
    }

    public List<ScheduleDTO> getScheduleList(ScheduleType type, int year, int month, Long memberId) {
        // 선택한 월의 앞뒤로 2주씩의 데이터를 같이 검색한다. --> fullCalendar에서 앞 뒤 월 데이터 일부를 함께 렌더링
        int beforeYear = (month == 1 ? year - 1 : year);
        int beforeMonth = (month == 1 ? 12 : month - 1);
        LocalDate lastDayOfBeforeMonth = YearMonth.from(LocalDate.of(beforeYear, beforeMonth, 1)).atEndOfMonth();
        int beforeWeekNumber = lastDayOfBeforeMonth.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        int nextYear = (month == 12 ? year + 1 : year);
        int nextMonth = (month == 12 ? 1 : month + 1);
        LocalDate firstDayOfNextMonth = LocalDate.of(nextYear, nextMonth, 1);
        int nextWeekNumber = firstDayOfNextMonth.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        List<Schedule> list = new ArrayList<>();

        list.addAll(getScheduleEntities(type, beforeYear, beforeMonth, beforeWeekNumber - 1, beforeWeekNumber));
        list.addAll(getScheduleEntities(type, year, month));
        list.addAll(getScheduleEntities(type, nextYear, nextMonth, nextWeekNumber, nextWeekNumber + 1));

        return list.stream()
            .filter(e -> {
                if(ScheduleType.PERSONAL.equals(type)) {  // 개인일정인 경우 내가 작성한 건은 다 보이고, 다른 멤버가 작성한 것은 외근, 직출퇴는 숨기기 --> 너무 많아서 숨김처리.
                    if(e.getMemberId().equals(memberId))
                        return true;
                    else if(!e.getMemberId().equals(memberId) && !e.getCode().equals("100") && !e.getCode().equals("110"))
                        return true;
                    else
                        return false;
                    
                } else {
                    return true;
                }
            })
            .map(elem ->
                scheduleConverter.getDto(elem).setCodeValue(basecodeService.getValue(BasecodeType.valueOf(type.name()), elem.getCode())).setContent(null)
                // list 반환시 content는 불필요해서 제거했다. 만약 퍼포먼스가 안 나오면 Repository 인터페이스에서 네이티브 쿼리로 content 자체를 불러오지 않도록 변경할 것.
        ).collect(Collectors.toList());
    }

    public ScheduleDTO getScheduleDto(Long id) {
        return scheduleConverter.getDto(scheduleRepo.findById(id).orElseGet(Schedule::new));
    }

/*
        개인 일정이 아닌 데이터의 입력은 기간 중복을 체크해야 한다.
        기간 체크 로직
        
        등록 데이터의 시작: o (small o)
        등록 데이터의 종료: O (capital o)

        기준:     시작          종료            결과
        -----------+-------------+---------->  false
        -----------o-------------O---------->  false
        -------o---+-------------O---------->  false
        -------o---+---------O---+---------->  false
        -------o---+-------------+----O----->  false
        -----------o-------------+----O----->  false
        -----------o---------O---+---------->  false
        -----------+---o-----O---+---------->  false
        -----------+---o---------+----O----->  false
        -----------+---------o---+----O----->  false
        ------o----O-------------+---------->  true
        -----------+-------------o----O----->  true

        정리하자면 가능한 케이스는 아래 두 개
        1. 등록 데이터의 [종료] 일정이 기준 데이터의 [시작] 일정보다 작거나 같은 경우  -->  in_S < in_E <= base_S
        2. 등록 데이터의 [시작] 일정이 기준 데이터의 [종료] 일정보다 같거나 큰 경우    -->  in_E > in_S >= base_E
    */

    private boolean isDuplicated(ScheduleType type, String code, LocalDateTime start, LocalDateTime end, Long id) {
        if(type.equals(ScheduleType.PERSONAL))
            return false;

        int sYear = start.getYear();
        int sMon = start.getMonthValue();
        int eYear = end.getYear();
        int eMon = end.getMonthValue();

        // 위에 주석으로 작성한 1, 2번에 해당하지 않는 데이터가 1개라도 있으면 false로 전환.
        boolean sDup = false;
        boolean eDup = false;
        boolean isSameMonth = (sYear == eYear) && (sMon == eMon);

        for(Schedule data : getScheduleEntities(type, sYear, sMon)) {
            if(data.getId().equals(id))
                continue;   // 이미 등록한 데이터를 업데이트할 때 자기자신의 일정을 체크하지 않아야 하므로.
            
            if(code.equals(data.getCode())) {   // 같은 code만 체크
                if(end.isBefore(data.getDatetimeStart()) || end.equals(data.getDatetimeStart()) || start.isAfter(data.getDatetimeEnd()) || start.equals(data.getDatetimeEnd()))
                    continue;
                else
                    sDup = true;
            }
        }

        if(isSameMonth)
            eDup = sDup;
        else {
            for(Schedule data : getScheduleEntities(type, eYear, eMon)) {
                if(data.getId().equals(id))
                    continue;   // 이미 등록한 데이터를 업데이트할 때 자기자신의 일정을 체크하지 않아야 하므로.
                
                if(code.equals(data.getCode())) {   // 같은 code만 체크
                    if(end.isBefore(data.getDatetimeStart()) || end.equals(data.getDatetimeStart()) || start.isAfter(data.getDatetimeEnd()) || start.equals(data.getDatetimeEnd()))
                        continue;
                    else
                        eDup = true;
                }
            }
        }
        return sDup || eDup;
    }

    @Async
    public void saveScheduleForDayoff(Document docs, List<DayoffDTO> dayoffDtoList) {   // 결재 문서 중 휴가 결재가 최종 승인될 때 등록하는 메서드로 비동기로 처리
        LocalDateTime start = null;
        LocalDateTime end = null;
        Member writer = docs.getMember().getMember();

        for(DayoffDTO dto : dayoffDtoList) {
            String scheduleCode = null;
            
            switch(dto.getCode()) {
                case "100":
                case "190":
                case "200":
                case "201":
                case "602":
                    scheduleCode = "130";
                    start = LocalDateTime.of(dto.getDateStart(), LocalTime.of(0, 0));
                    end = LocalDateTime.of(dto.getDateEnd(), LocalTime.of(23, 59));
                    break;
                
                case "110":
                    scheduleCode = "131";
                    start = LocalDateTime.of(dto.getDateStart(), LocalTime.of(8, 30));
                    end = LocalDateTime.of(dto.getDateEnd(), LocalTime.of(12, 30));
                    break;

                case "120":
                    scheduleCode = "132";
                    start = LocalDateTime.of(dto.getDateStart(), LocalTime.of(13, 30));
                    end = LocalDateTime.of(dto.getDateEnd(), LocalTime.of(17, 30));
                    break;

                default: break;
            }
            
            if(scheduleCode == null)
                continue;
            
            String codeValue = null;
            switch(dto.getCode()) {
                case "100": codeValue = "연차"; break;
                case "110": codeValue = "반차(오전)"; break;
                case "120": codeValue = "반차(오후)"; break;
                case "190": codeValue = "장기근속휴가"; break;
                case "200": codeValue = "대체휴가"; break;
                case "201": codeValue = "단축근무일"; break;
                case "602": codeValue = "훈련"; break;
            }
            saveSchedule(
                new ScheduleDTO().setType(ScheduleType.PERSONAL).setCode(scheduleCode).setDatetimeStart(start).setDatetimeEnd(end)
                    .setTitle(docs.getTitle())
                    .setContent(
                        new StringBuilder()
                            .append("시스템에서 자동 등록한 일정입니다.")
                            .append(System.lineSeparator())
                            .append("휴가 신청서 문서번호 [ ").append(docs.getId().toString()).append(" ]")
                            .append(System.lineSeparator())
                            .append("휴가 코드: ").append(codeValue)
                        .toString()
                    ),
                writer
            );
        }
    }

    public RequestResult saveSchedule(ScheduleDTO dto, Member member) {
        if(dto.getTitle() == null || dto.getTitle().strip().isBlank())
            return RequestResult.getDefaultFail("제목을 작성하세요.");
        
        if(dto.getCode() == null || dto.getCode().strip().isBlank())
            return RequestResult.getDefaultFail("등록할 일정 코드를 선택하세요.");

        if(dto.getDatetimeStart() == null)
            return RequestResult.getDefaultFail("시작 일시를 선택하세요.");
        if(dto.getDatetimeEnd() == null)
            return RequestResult.getDefaultFail("종료 일시를 선택하세요.");

        if(dto.getDatetimeStart().equals(dto.getDatetimeEnd()))
            return RequestResult.getDefaultFail("시작 일시와 종료 일시가 같습니다.");

        if(dto.getDatetimeStart().isAfter(dto.getDatetimeEnd()))
            return RequestResult.getDefaultFail("시작 일시가 종료 일시보다 이후 입니다.");
        if(dto.getDatetimeEnd().isBefore(dto.getDatetimeStart()))
            return RequestResult.getDefaultFail("종료 일시가 시작 일시보다 이전 입니다.");

        if(dto.getContent().length() > Constants.COLUMN_LENGTH_REMARKS)
            return RequestResult.getDefaultFail("내용을 " + Constants.COLUMN_LENGTH_REMARKS + "자 이내로 작성하세요.");
        
        dto.setType(dto.getType()).setTitle(dto.getTitle().strip())
            .setContent(
                (dto.getContent() == null || dto.getContent().strip().isBlank()) ? null : dto.getContent().strip()
            );
        
        Schedule entity;
        String returnMsg = null;

        if(dto.getId() == null) {
            if(dto.getType() == null)
                return RequestResult.getDefaultFail("등록할 일정 유형을 선택하세요.");

            if(isDuplicated(dto.getType(), dto.getCode(), dto.getDatetimeStart(), dto.getDatetimeEnd(), dto.getId()))
                return RequestResult.getDefaultFail("먼저 등록된 예약이 있습니다. 날짜를 변경하세요.");

            entity = scheduleConverter.getEntity(dto).setWriter(memberService.searchMemberDetailsById(member.getId()));
            returnMsg = Constants.RESULT_MESSAGE_INSERTED;

        } else {
            entity = scheduleRepo.getById(dto.getId());
            if(entity == null)
                return RequestResult.getDefaultFail("수정할 데이터가 없습니다.");

            if(!entity.getMemberId().equals(member.getId()))
                return RequestResult.getDefaultFail("다른 멤버가 등록한 데이터는 수정할 수 없습니다.");
            
            if(isDuplicated(entity.getType(), dto.getCode(), dto.getDatetimeStart(), dto.getDatetimeEnd(), dto.getId()))
                return RequestResult.getDefaultFail("먼저 등록된 예약이 있습니다. 날짜를 변경하세요.");

            entity.updateTitle(dto.getTitle()).updateContent(dto.getContent()).updateCode(dto.getCode())
                .updateStart(dto.getDatetimeStart()).updateEnd(dto.getDatetimeEnd());
            
            returnMsg = Constants.RESULT_MESSAGE_UPDATED;
        }
        scheduleRepo.save(entity.updateYearAndMonth());
        
        if(entity.getType().equals(ScheduleType.COMPANY)) {   // 회사 일정인 경우 sse로 전달
            sseService.sendToClients(SseData.COMPANY);
        }

        return RequestResult.getDefaultSuccess(returnMsg);
    }

    public RequestResult deleteSchedule(Long id, Member member) {
        if(id == null)
            return RequestResult.getDefaultFail("데이터가 없습니다.");
        
        Schedule entity = scheduleRepo.getById(id);
        if(entity == null)
            return RequestResult.getDefaultFail("데이터가 없습니다.");
        
        if(entity.getMemberId().equals(member.getId())) {
            scheduleRepo.delete(entity);

            if(entity.getType().equals(ScheduleType.COMPANY)) {   // 회사 일정인 경우 sse로 전달
                sseService.sendToClients(SseData.COMPANY);
            }

            return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_DELETED);
        
        } else {
            return RequestResult.getDefaultFail("다른 멤버가 등록한 데이터는 삭제할 수 없습니다.");
        }
    }
}
