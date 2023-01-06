package com.project.simplegw.document.approval.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.ApproverLine;

@Repository
public interface ApproverLineRepo extends JpaRepository<ApproverLine, Long> {
    List<ApproverLine> findByOwnerId(Long ownerId);
}
