package com.project.simplegw.document.approval.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.ApprovalStatus;
import com.project.simplegw.document.vos.DocsType;

@Repository
public interface ApprovalStatusRepo extends JpaRepository<ApprovalStatus, Long> {
    Optional<ApprovalStatus> findByDocsId(Long docsId);


    @Query(
        value = """
                    select
                        b.id, b.type, b.title,
                        b.writer_team, b.writer_job_title, b.writer_name,
                        approver_team = a.team, approver_job_title = a.job_title, approver_name = a.name,
                        a.sign, b.created_date,
                        b.writer_id
                    from approval_status a
                        join docs b on a.docs_id = b.id
                    where 1=1
                        and b.writer_id = :#{#writer_id}
                        and b.created_date between :#{#date_from} and :#{#date_to}
                        and a.finished = '1'
                        and b.type = case when :#{#type.name()} = 'ALL' then b.type else :#{#type.name()} end
                    order by b.id desc
                """,
        nativeQuery = true
    )
    List<Object[]> findFinished(@Param("writer_id") Long writerId, @Param("date_from") LocalDate dateFrom, @Param("date_to") LocalDate dateTo, @Param("type") DocsType type);
}
