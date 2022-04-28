package com.project.simplegw.document.repositories;

import java.util.List;

import com.project.simplegw.document.entities.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByDocsIdOrderById(Long docsId);
}
