package com.project.simplegw.document.approval.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.Approver;
import com.project.simplegw.document.vos.DocsType;

@Repository
public interface ApproverRepo extends JpaRepository<Approver, Long> {
    List<Approver> findByDocsIdOrderBySeq(Long docsId);


    // 결재요청으로 받은 문서 기간 검색, 결과를 DtosApprovalDocsMin 클래스로 처리하기 위해서 List<Object[]>로 반환
    @Query(
        value = """
                    select
                        b.id, b.type, b.title,
                        b.writer_team, b.writer_job_title, b.writer_name,
                        approver_team = c.team, approver_job_title = c.job_title, approver_name = c.name,
                        c.sign, b.created_date,
                        b.writer_id
                    from approver a
                        join docs b on a.docs_id = b.id
                        join approval_status c on b.id = c.docs_id
                    where 1=1
                        and a.member_id = :#{#approver_id}
                        and b.type = case when :#{#type.name()} = 'ALL' then b.type else :#{#type.name()} end
                        and b.created_date between :#{#date_from} and :#{#date_to}
                    order by b.id desc
                """,
        nativeQuery = true
    )
    List<Object[]> findForApprover(@Param("approver_id") Long approverId, @Param("type") DocsType type, @Param("date_from") LocalDate dateFrom, @Param("date_to") LocalDate dateTo);




    // 결재문서 검색, 기준이 결재문서 작성자이므로 ApproverRepo에서 처리.
    @Query(
        value = """
                    select
                        a.id, a.type, a.title,
                        a.writer_team, a.writer_job_title, a.writer_name,
                        approver_team = b.team, approver_job_title = b.job_title, approver_name = b.name,
                        b.sign, a.created_date,
                        a.writer_id
                    from docs a
                        join approval_status b on a.id = b.docs_id
                    where 1=1
                        and a.type = case when :#{#type.name()} = 'ALL' then a.type else :#{#type.name()} end
                        and a.created_date between :#{#date_from} and :#{#date_to}
                """,
        nativeQuery = true
    )
    List<Object[]> getApprovalDocs(@Param("type") DocsType type, @Param("date_from") LocalDate dateFrom, @Param("date_to") LocalDate dateTo);


    // 문서번호로 검색
    @Query(
        value = """
                    select
                        a.id, a.type, a.title,
                        a.writer_team, a.writer_job_title, a.writer_name,
                        approver_team = b.team, approver_job_title = b.job_title, approver_name = b.name,
                        b.sign, a.created_date,
                        a.writer_id
                    from docs a
                        join approval_status b on a.id = b.docs_id
                    where 1=1
                        and b.docs_id = :#{#docs_id}
                """,
        nativeQuery = true
    )
    List<Object[]> getApprovalDocs(@Param("docs_id") Long docsId);
}
