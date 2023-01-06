package com.project.simplegw.document.approval.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.ApproverLineDetails;

@Repository
public interface ApproverLineDetailsRepo extends JpaRepository<ApproverLineDetails, Long> {
    List<ApproverLineDetails> findByMasterId(Long masterId);
}
