package com.project.simplegw.document.approval.repositories.details.dayoff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.details.dayoff.TempDayoff;

@Repository
public interface TempDayoffRepo extends JpaRepository<TempDayoff, Long> {
    List<TempDayoff> findByDocsId(Long docsId);
}
