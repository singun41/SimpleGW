package com.project.simplegw.member.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.member.entities.MemberAddOn;

@Repository
public interface MemberAddOnRepo extends JpaRepository<MemberAddOn, Long> {
    Optional<MemberAddOn> findByMemberId(Long memberId);
}
