package com.project.simplegw.approval.repositories;

import java.util.List;

import com.project.simplegw.approval.entities.TemplateLineMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateLineMasterRepository extends JpaRepository<TemplateLineMaster, Long> {
    List<TemplateLineMaster> findAllByOwnerId(Long ownerId);
}
