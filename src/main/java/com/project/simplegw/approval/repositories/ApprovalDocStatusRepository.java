package com.project.simplegw.approval.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.approval.entities.ApprovalDocStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApprovalDocStatusRepository extends JpaRepository<ApprovalDocStatus, Long> {
    ApprovalDocStatus getByDocsId(Long docsId);
    List<ApprovalDocStatus> getByWriterIdAndFinished(Long writerId, boolean isFinished);

    // 진행중인 결재문서 --> is_finished = 0 and document.registered = 1
    // 진행중인 결재문서 카운트
    @Query(value = "select count(a.docs_id) "
                    + "from sgw_approval_docs_status a "
                        + "join sgw_document b on a.docs_id = b.id and b.member_id = :#{#id} and b.type = 'APPROVAL' "
                    + "where a.writer_id = :#{#id} and a.is_finished = '0' and b.registered = '1'",
        nativeQuery = true)
    long proceedingDocsCount(@Param("id") Long writerId);

    // 진행중인 결재문서 리스트
    @Query(value = "select id = a.docs_id, b.kind, b.title, approver_team = a.team, approver_job_title = a.job_title, approver_name = a.name, a.status, b.created_date "
                    + "from sgw_approval_docs_status a "
                        + "join sgw_document b on a.docs_id = b.id and b.member_id = :#{#id} and b.type = 'APPROVAL' "
                    + "where a.writer_id = :#{#id} and a.is_finished = '0' and b.registered = '1'",
        nativeQuery = true)
    List<Object[]> findAllProceedingDocs(@Param("id") Long writerId);

    // 완결된 결재문서 리스트 --> is_finished = 1
    @Query(value = "select id = a.docs_id, b.kind, b.title, approver_team = a.team, approver_job_title = a.job_title, approver_name = a.name, a.status, b.created_date "
                    + "from sgw_approval_docs_status a "
                        + "join sgw_document b on a.docs_id = b.id and b.member_id = :#{#id} and b.type = 'APPROVAL' and b.kind = b.kind "   // index 순서대로 작성함.
                            + "and b.created_date between :#{#start} and :#{#end} "
                    + "where a.writer_id = :#{#id} and a.is_finished = '1' and b.registered = '1'",
        nativeQuery = true)
    List<Object[]> findAllFinishedDocs(@Param("id") Long writerId, @Param("start") LocalDate dateStart, @Param("end") LocalDate dateEnd);
}
