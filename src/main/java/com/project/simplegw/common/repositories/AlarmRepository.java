package com.project.simplegw.common.repositories;

import java.util.List;

import com.project.simplegw.common.entities.Alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByMemberIdOrderByIdDesc(Long memberId);
}
