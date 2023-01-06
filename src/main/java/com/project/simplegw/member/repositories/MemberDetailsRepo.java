package com.project.simplegw.member.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.member.entities.MemberDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberDetailsRepo extends JpaRepository<MemberDetails, Long> {
    Optional<MemberDetails> findByMemberId(Long memberId);   // Member 클래스의 id
    List<MemberDetails> findByResignedOrderByName(boolean isResigned);
}
