package com.project.simplegw.approval.repositories;

import java.util.List;

import com.project.simplegw.approval.entities.Namecard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamecardRepository extends JpaRepository<Namecard, Long> {
    List<Namecard> findAllByDocsId(Long docsId);
    void deleteByDocsId(Long docsId);
}
