package com.project.simplegw.member.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.member.entities.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);  // findById는 member 테이블의 no(pk) 컬럼을 이용하는 메서드이고, 이건 userId를 이용하는 메서드이다.
    List<Member> findAllByEnabled(boolean enabled);
}
