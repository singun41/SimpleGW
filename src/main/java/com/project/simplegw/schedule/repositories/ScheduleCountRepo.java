package com.project.simplegw.schedule.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.schedule.entities.ScheduleCount;

@Repository
public interface ScheduleCountRepo extends JpaRepository<ScheduleCount, Long> {
    List<ScheduleCount> findByDate(LocalDate date);
    List<ScheduleCount> findByScheduleId(Long scheduleId);

    // Optional<ScheduleCount> findByDateAndMemberId(LocalDate date, Long memberId);
    // List<ScheduleCount> findByDateBetweenAndMemberId(LocalDate from, LocalDate to, Long memberId);
}
