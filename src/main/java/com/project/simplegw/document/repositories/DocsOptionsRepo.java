package com.project.simplegw.document.repositories;

import java.util.Optional;

import com.project.simplegw.document.entities.DocsOptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsOptionsRepo extends JpaRepository<DocsOptions, Long> {
    Optional<DocsOptions> findByDocsId(Long docsId);
}
