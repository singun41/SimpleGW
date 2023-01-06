package com.project.simplegw.upload.repositories;

import java.util.List;

import com.project.simplegw.upload.entities.Attachments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentsRepo extends JpaRepository<Attachments, Long> {
    List<Attachments> findByDocsIdOrderBySeq(Long docsId);
    long countByDocsId(Long docsId);
}
