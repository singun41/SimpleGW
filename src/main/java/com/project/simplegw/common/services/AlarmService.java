package com.project.simplegw.common.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.common.entities.Alarm;
import com.project.simplegw.common.repositories.AlarmRepository;
import com.project.simplegw.system.services.SseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class AlarmService {
    private final AlarmRepository alarmRepo;
    private final SseService sseService;

    @Autowired
    public AlarmService(AlarmRepository alarmRepo, SseService sseService) {
        this.alarmRepo = alarmRepo;
        this.sseService = sseService;
    }

    @Async
    public void insertNewAlarm(Long memberId, String content) {
        alarmRepo.save(Alarm.builder().memberId(memberId).content(content).build());
        sseService.sendAlarm(memberId);
    }

    private List<Alarm> getAlarmListNotChecked(Long memberId) {
        return alarmRepo.findAllByMemberIdOrderByIdDesc(memberId).stream().filter(entity -> entity.getCheckedDatetime() == null).collect(Collectors.toList());
    }

    @Async
    public void updateAlarmChecked(Long memberId) {
        alarmRepo.saveAll(getAlarmListNotChecked(memberId).stream().map(Alarm::updateCheckedDatetime).collect(Collectors.toList()));
    }

    public long getAlarmNotCheckedCount(Long memberId) {
        return getAlarmListNotChecked(memberId).stream().count();
    }

    public List<String> getAlarmContent(Long memberId) {
        return alarmRepo.findAllByMemberIdOrderByIdDesc(memberId).stream().map(Alarm::getContent).collect(Collectors.toList());
    }

    public void clearAlarms() {
        alarmRepo.deleteAll(
            alarmRepo.findAll().stream().filter(entity ->
                entity.getCheckedDatetime() != null && entity.getCheckedDatetime().toLocalDate().isBefore(LocalDate.now().minusDays(7))
            ).collect(Collectors.toList())
        );
    }
}
