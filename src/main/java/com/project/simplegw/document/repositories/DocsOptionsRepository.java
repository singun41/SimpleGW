package com.project.simplegw.document.repositories;

import com.project.simplegw.document.entities.DocsOptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsOptionsRepository extends JpaRepository<DocsOptions, Long> {
    DocsOptions getByDocsId(Long docsId);
}
