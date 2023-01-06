package com.project.simplegw.schedule.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.schedule.entities.Schedule;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDateFromBetweenOrderById(LocalDate from, LocalDate to);
}
