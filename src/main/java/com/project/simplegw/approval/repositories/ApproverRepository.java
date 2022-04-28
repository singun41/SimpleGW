package com.project.simplegw.approval.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.approval.entities.Approver;
import com.project.simplegw.document.vos.DocumentKind;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
    void deleteByDocsId(Long docsId);
    List<Approver> findAllByDocsId(Long docsId);

    // 결재순번에 도착해 있는 경우는 status가 항상 PROCEED 이다.
    @Query(value = "select count(a.docs_id) "
                    + "from sgw_approver a "
                        + "join sgw_document b on a.docs_id = b.id and b.type = 'APPROVAL' and b.registered = '1' "
                        + "join sgw_approval_docs_status c on b.id = c.docs_id and c.is_finished = '0' "
                    + "where a.approver_id = :#{#id} and a.seq > 0 and a.status = 'PROCEED'",
        nativeQuery = true)
    long countReceivedAprovalDocs(@Param("id") Long approverId);
    
    // 받은 결재문서 리스트를 엔티티가 아닌 ReceievedDocsDTO로 바로 바인딩하기 위해서 List<Object[]> 로 반환한다.
    // ReceievedDocsDTO에 Object[]를 파라미터로 받는 커스텀 생성자가 작성돼 있다.
    // 현재 결재요청 문서 리스트
    @Query(value = "select c.kind, a.docs_id, c.writer_job_title, c.writer_name, c.title, b.status, approver_job_title = b.job_title, approver_mame = b.name, c.created_date "
                    + "from sgw_approver a "
                        + "join sgw_approval_docs_status b on a.docs_id = b.docs_id and b.is_finished = '0' "
                        + "join sgw_document c on a.docs_id = c.id and c.type = 'APPROVAL' and c.kind = case when :#{#kind.name()} = 'ALL' then c.kind else :#{#kind.name()} end "
                            + "and c.registered = '1'"
                    + "where a.approver_id = :#{#id} and a.seq > 0 and a.status = 'PROCEED'",
        nativeQuery = true)
    List<Object[]> findAllReceivedApprovalDocs(@Param("id") Long approverId, @Param("kind") DocumentKind kind);


    // 결재 요청 받은 문서 리스트 : 기간 조회
    @Query(value = "select c.kind, a.docs_id, c.writer_job_title, c.writer_name, c.title, b.status, approver_job_title = b.job_title, approver_name = b.name, c.created_date "
                    + "from sgw_approver a "
                        + "join sgw_approval_docs_status b on a.docs_id = b.docs_id "
                        + "join sgw_document c on a.docs_id = c.id and c.type = 'APPROVAL' and c.kind = case when :#{#kind.name()} = 'ALL' then c.kind else :#{#kind.name()} end "
                            + "and c.registered = '1'"
                    + "where a.approver_id = :#{#id} and a.seq > 0 and a.status != 'SUBMITTED'"
                        + "and c.created_date between :#{#start} and :#{#end}",
        nativeQuery = true)
    List<Object[]> findAllReceivedApprovalDocs(@Param("id") Long approverId, @Param("kind") DocumentKind kind, @Param("start") LocalDate dateStart, @Param("end") LocalDate dateEnd);
}
