package com.project.simplegw.work.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.work.entities.WorkRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRecordRepository extends JpaRepository<WorkRecord, Long> {
    WorkRecord getByWorkDateAndMemberDetailsId(LocalDate workDate, Long id);
    List<WorkRecord> findAllByWorkDateOrderByTeamAscNameAsc(LocalDate workDate);
    List<WorkRecord> findAllByWorkDateAndTeamOrderByName(LocalDate workDate, String team);
}
