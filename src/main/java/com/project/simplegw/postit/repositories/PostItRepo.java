package com.project.simplegw.postit.repositories;

import java.util.List;

import com.project.simplegw.postit.entities.PostIt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostItRepo extends JpaRepository<PostIt, Long> {
    List<PostIt> findByMemberId(Long memberId);
}
