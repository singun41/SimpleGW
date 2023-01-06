package com.project.simplegw.document.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.document.entities.TempDocs;
import com.project.simplegw.document.vos.DocsType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempDocsRepo extends JpaRepository<TempDocs, Long> {
    List<TempDocs> findByWriterId(Long writerId);
    long countByWriterId(Long writerId);
    Optional<TempDocs> findByIdAndType(Long docsId, DocsType type);
}
