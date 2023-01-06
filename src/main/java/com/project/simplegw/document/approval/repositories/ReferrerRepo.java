package com.project.simplegw.document.approval.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.Referrer;
import com.project.simplegw.document.vos.DocsType;

@Repository
public interface ReferrerRepo extends JpaRepository<Referrer, Long> {
    List<Referrer> findByDocsIdOrderById(Long docsId);
    List<Referrer> findByMemberIdOrderById(Long memberId);

    
    @Query(   // 일반문서 회의록 공유기능이 추가, 회의록은 제외한 결재문서만 카운트
        value = """
                    select count(a.id)
                    from referrer a
                        join docs b on a.docs_id = b.id
                    where 1=1
                        and a.member_id = :#{#member_id} and a.checked_datetime is null
                        and b.[type] not in ('MINUTES')
                """,
        nativeQuery = true
    )
    long countByMemberIdAndCheckedDatetimeIsNull(@Param("member_id") Long memberId);   // 참조요청 받은 확인하지 않은 문서 카운트


    // 결재참조로 받은 문서 기간 검색, 결과를 DtosApprovalDocsMin 클래스로 처리하기 위해서 List<Object[]>로 반환
    @Query(
        value = """
                    select
                        b.id, b.type, b.title,
                        b.writer_team, b.writer_job_title, b.writer_name,
                        approver_team = c.team, approver_job_title = c.job_title, approver_name = c.name,
                        c.sign, b.created_date,
                        b.writer_id
                    from referrer a
                        join docs b on a.docs_id = b.id
                        join approval_status c on b.id = c.docs_id
                    where 1=1
                        and a.member_id = :#{#referrer_id}
                        and b.[type] = case when :#{#type.name()} = 'ALL' then b.[type] else :#{#type.name()} end
                        and b.created_date between :#{#date_from} and :#{#date_to}
                    order by b.id desc
                """,
        nativeQuery = true
    )
    List<Object[]> findForReferrer(@Param("referrer_id") Long referrerId, @Param("type") DocsType type, @Param("date_from") LocalDate dateFrom, @Param("date_to") LocalDate dateTo);
}
