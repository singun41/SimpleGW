package com.project.simplegw.work.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.work.entities.WorkRecord;

@Repository
public interface WorkRecordRepo extends JpaRepository<WorkRecord, Long> {
    Optional<WorkRecord> findTop1ByWorkDateLessThanAndMemberIdOrderByWorkDateDesc(LocalDate workDate, Long memberId);
    Optional<WorkRecord> findByWorkDateAndMemberId(LocalDate workDate, Long memberId);
    List<WorkRecord> findByWorkDate(LocalDate workDate);
}
