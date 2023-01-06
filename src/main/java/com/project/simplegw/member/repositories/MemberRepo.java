package com.project.simplegw.member.repositories;

import java.util.List;
import java.util.Optional;

import com.project.simplegw.member.entities.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
    Optional<Member> findByUserId(String userId);


    @Query(
        value = """
                    select a.id, a.user_id, a.password, a.role, a.enabled
                    from member a
                        join member_details b on a.id = b.member_id
                    where b.resigned = :#{#resigned}
                """,
        nativeQuery = true
    )
    List<Member> findByResignedMember(@Param("resigned") boolean resigned);
}
