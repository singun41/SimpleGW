package com.project.simplegw.schedule.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.project.simplegw.schedule.entities.Schedule;
import com.project.simplegw.schedule.vos.ScheduleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule getById(Long id);
    List<Schedule> findAllByTypeAndYearAndMonth(ScheduleType type, int year, int month);
    List<Schedule> findAllByTypeAndYearAndMonthAndWeekOfYearBetween(ScheduleType type, int year, int month, int startWeekNumber, int endWeekNumber);
    List<Schedule> findTop5ByTypeAndYearAndMonthAndDatetimeStartGreaterThanEqualOrderByDatetimeStart(ScheduleType type, int year, int month, LocalDateTime now);
}
