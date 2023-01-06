package com.project.simplegw.document.repositories;

import java.util.List;
// import java.util.Optional;

import com.project.simplegw.document.entities.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    // Optional<Comment> findByDocsId(Long docsId);   업데이트 기능은 제공하지 않기 때문에 개별 엔티티 조회는 하지 않음.
    List<Comment> findByDocsIdOrderById(Long docsId);
}
