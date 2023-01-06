package com.project.simplegw.alarm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.alarm.entities.Alarm;

@Repository
public interface AlarmRepo extends JpaRepository<Alarm, Long> {
    List<Alarm> findByMemberId(Long memberId);
}
