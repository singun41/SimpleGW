package com.project.simplegw.approval.repositories;

import java.util.List;

import com.project.simplegw.approval.entities.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByDocsIdOrderBySeq(Long docsId);
    void deleteByDocsId(Long docsId);
}
