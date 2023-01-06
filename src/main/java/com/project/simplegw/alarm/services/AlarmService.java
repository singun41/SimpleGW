package com.project.simplegw.alarm.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.project.simplegw.alarm.dtos.receive.DtorAlarm;
import com.project.simplegw.alarm.dtos.send.DtosAlarm;
import com.project.simplegw.alarm.entities.Alarm;
import com.project.simplegw.alarm.helpers.AlarmConverter;
import com.project.simplegw.alarm.repositories.AlarmRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class AlarmService {
    private final AlarmRepo repo;
    private final AlarmConverter converter;

    @Autowired
    public AlarmService(AlarmRepo repo, AlarmConverter converter) {
        this.repo = repo;
        this.converter = converter;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    
    private List<Alarm> getEntities(LoginUser loginUser) {
        return repo.findByMemberId( loginUser.getMember().getId() );
    }

    public DtosAlarm getAlarm(Long id) {
        return converter.getDto( repo.findById(id).orElseGet(Alarm::new) );
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_ALARMS, key = "#loginUser.getMember().getId()")
    public List<DtosAlarm> getAlarms(LoginUser loginUser) {
        return getEntities(loginUser).stream().map(converter::getDto).collect(Collectors.toList());
    }

    // 유저별 알람 리스트와 유저별 오늘날짜의 알람 리스트를 같이 캐싱하기 위해서 이 메서드를 주석처리하고, 컨트롤러에서 스트림 필터로 재집계한다.
    // public List<DtosAlarm> getTodayAlarms(LoginUser loginUser) {
    //     return getEntities(loginUser).stream().filter( e -> e.getAlarmDate().equals(LocalDate.now()) ).map(converter::getDto).collect(Collectors.toList());
    // }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_ALARMS, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceMsg create(DtorAlarm dto, LoginUser loginUser) {
        try {
            Alarm alarm = Alarm.builder().memberId( loginUser.getMember().getId() ).title( dto.getTitle() ).remarks( dto.getRemarks() ).build();
            alarm.updateAlarmDate( dto.getAlarmDate() ).updateAlarmTime( dto.getAlarmTime() );   // NPE를 피하기 위해 build() 이후에 메서드로 처리.
            repo.save(alarm);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("create alarm exception.");
            log.warn("parameters: {}, user: {}", dto.toString(), loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("알람 등록 에러입니다. 관리자에게 문의하세요.");
        }
    }

    
    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_ALARMS, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceMsg update(Long alarmId, DtorAlarm dto, LoginUser loginUser) {
        try {
            Optional<Alarm> target = repo.findById(alarmId);
            if(target.isPresent()) {
                Alarm alarm = target.get();

                if( ! alarm.getMemberId().equals(loginUser.getMember().getId()) )
                    return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("다른 유저의 알람은 업데이트할 수 없습니다.");

                alarm.updateAlarmDate( dto.getAlarmDate() ).updateAlarmTime( dto.getAlarmTime() ).updateTitle( dto.getTitle() ).updateRemarks( dto.getRemarks() );
                repo.save(alarm);
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("업데이트 대상 알람 데이터가 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update alarm exception.");
            log.warn("alarmId: {}, parameters: {}, user: {}", alarmId, dto.toString(), loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("알람 수정 에러입니다. 관리자에게 문의하세요.");
        }
    }


    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_ALARMS, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceMsg delete(Long alarmId, LoginUser loginUser) {
        try {
            Optional<Alarm> target = repo.findById(alarmId);
            if(target.isPresent()) {
                Alarm alarm = target.get();

                if( ! alarm.getMemberId().equals(loginUser.getMember().getId()) )
                    return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("다른 유저의 알람은 삭제할 수 없습니다.");

                repo.delete(alarm);
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
            
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("삭제 대상 알람 데이터가 없습니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("update alarm exception.");
            log.warn("alarmId: {}, user: {}", alarmId, loginUser.getMember().getId());

            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("알람 삭제 에러입니다. 관리자에게 문의하세요.");
        }
    }
}
