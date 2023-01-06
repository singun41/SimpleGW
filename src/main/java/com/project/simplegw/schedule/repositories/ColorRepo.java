package com.project.simplegw.schedule.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.schedule.entities.Color;
import com.project.simplegw.schedule.vos.ScheduleType;

@Repository
public interface ColorRepo extends JpaRepository<Color, Long> {
    List<Color> findByTypeOrderByCode(ScheduleType type);
}
