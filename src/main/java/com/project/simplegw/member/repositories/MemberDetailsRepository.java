package com.project.simplegw.member.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.member.entities.MemberDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {
    // Optional<MemberDetails> findByMemberNo(int memberNo);   // 단일 객체는 Optional을 써서 null 체크
    // List<MemberDetails> findByTeam(String team);   // 컬렉션 반환은 데이터가 없을 경우 빈 컬렉션을 리턴한다. 그래서 Optional로 감쌀 필요가 없다.
    Optional<MemberDetails> findByMemberId(Long memberId);   // @Id값인 id 필드가 아닌 Member 클래스의 id로 찾는다.
    List<MemberDetails> findAllByRetired(boolean isRetired);
}
