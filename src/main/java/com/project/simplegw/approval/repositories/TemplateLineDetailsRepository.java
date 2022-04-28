package com.project.simplegw.approval.repositories;

import java.util.List;

import com.project.simplegw.approval.entities.TemplateLineDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateLineDetailsRepository extends JpaRepository<TemplateLineDetails, Long> {
    List<TemplateLineDetails> findAllByMasterId(Long masterId);
}
