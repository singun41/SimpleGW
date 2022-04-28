package com.project.simplegw.approval.repositories;

import java.util.List;

import com.project.simplegw.approval.entities.Dayoff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayoffRepository extends JpaRepository<Dayoff, Long> {
    List<Dayoff> findAllByDocsIdOrderBySeq(Long docsId);
    void deleteByDocsId(Long docsId);
}
