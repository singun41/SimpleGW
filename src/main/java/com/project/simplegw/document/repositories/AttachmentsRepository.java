package com.project.simplegw.document.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.document.entities.Attachments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentsRepository extends JpaRepository<Attachments, Long> {
    Optional<Attachments> findByDocsIdAndSeqAndConversionName(Long docsId, int seq, String conversionName);
    List<Attachments> findAllByDocsIdOrderBySeq(Long docsId);
    long countByDocsId(Long docsId);
}
