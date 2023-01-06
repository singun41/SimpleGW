package com.project.simplegw.schedule.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.schedule.dtos.send.DtosScheduleMember;
import com.project.simplegw.schedule.dtos.send.DtosScheduleSummary;
import com.project.simplegw.schedule.entities.Schedule;
import com.project.simplegw.schedule.entities.ScheduleCount;
import com.project.simplegw.schedule.helpers.ScheduleConverter;
import com.project.simplegw.schedule.repositories.ScheduleCountRepo;
import com.project.simplegw.schedule.repositories.ScheduleRepo;
import com.project.simplegw.schedule.vos.ScheduleFixedPersonalCode;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ScheduleCountService {
    private final ScheduleCountRepo repo;
    private final MemberService memberService;
    private final ScheduleRepo scheduleRepo;
    private final ScheduleConverter converter;
    private final ScheduleCountCacheService cacheService;

    @Autowired
    public ScheduleCountService(ScheduleCountRepo repo, MemberService memberService, ScheduleRepo scheduleRepo, ScheduleConverter converter, ScheduleCountCacheService cacheService) {
        this.repo = repo;
        this.memberService = memberService;
        this.scheduleRepo = scheduleRepo;
        this.converter = converter;
        this.cacheService = cacheService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    
    private List<ScheduleCount> todayList() {
        return repo.findByDate(LocalDate.now());
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_SCHEDULE_TODAY_SUMMARY)
    public DtosScheduleSummary getTodayScheduleCount() {
        log.info("Cacheable method getTodayCount() called.");

        DtosScheduleSummary summary = new DtosScheduleSummary();
        List<ScheduleCount> list = todayList();
        
        return summary
            .setDayoff(
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.DAYOFF.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count()
            )
            .setHalf(
                // 멤버 A가 오전반차, 오후반차 둘 다 사용하면 둘 다 표시되어야 하므로 
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.HALF_AM.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count() +
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.HALF_PM.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count()
            )
            .setOutOnBusiness(
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.OUT_ON_BUSINESS.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count()
            )
            .setBusinessTrip(
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.BUSINESS_TRIP.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count()
            )
            .setEducation(
                list.stream().filter(e -> e.getCode().equals(ScheduleFixedPersonalCode.EDUCATION.getCode())).mapToLong(ScheduleCount::getMemberId).distinct().count()
            );
    }


    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_SCHEDULE_TODAY_LIST)
    public List<DtosScheduleMember> getTodayScheduleMemberList() {
        List<ScheduleCount> list = todayList();
        List<DtosScheduleMember> summary = new ArrayList<>();

        Arrays.stream(ScheduleFixedPersonalCode.values()).forEach(scheduleCode -> {
            summary.addAll(
                list.stream()
                    .filter(e -> e.getCode().equals(scheduleCode.getCode()))
                    .map(e -> converter.getScheduleMember(memberService.getMemberData(e.getMemberId())).setType(scheduleCode.getTitle()))
                .collect(Collectors.toList())
            );
        });

        return summary;
    }



    void create(Schedule schedule) {
        LocalDate from = schedule.getDateFrom();
        LocalDate to = schedule.getDateTo();

        from.datesUntil(to.plusDays(1L)).forEach(date -> {
            ScheduleCount entity = ScheduleCount.builder().schedule(schedule).memberId(schedule.getMemberId()).code(schedule.getCode()).date(date).build();
            repo.save(entity);
        });

        if(from.equals(LocalDate.now()))
            cacheService.clear();
    }

    @Async
    public void create(Long scheduleId) {
        scheduleRepo.findById(scheduleId).ifPresent(entity -> create(entity));
    }


    @Async
    public void update(Long scheduleId, LoginUser loginUser) {
        Optional<Schedule> target = scheduleRepo.findById(scheduleId);
        if(target.isPresent()) {
            Schedule schedule = target.get();

            List<ScheduleCount> entities = repo.findByScheduleId(schedule.getId());
            if(entities.isEmpty())
                return;
            
            if( ! entities.get(0).getCode().equals(schedule.getCode()) ) {   // 코드가 달라진 경우만 업데이트.
                entities.stream().forEach(e -> e.updateCode(schedule));
                repo.saveAll(entities);
            }

            if(schedule.getDateFrom().equals(LocalDate.now()))
                cacheService.clear();
        }
    }




    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- called from SchedulerService ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public void removeOldScheduleCountEntities() {
        repo.deleteAllInBatch(
            repo.findAll().stream().filter(e -> e.getDate().isBefore(LocalDate.now())).collect(Collectors.toList())
        );
        log.info("Old ScheduleCount data removed.");
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- called from SchedulerService ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
