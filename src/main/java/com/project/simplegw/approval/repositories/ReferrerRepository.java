package com.project.simplegw.approval.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.approval.entities.Referrer;
import com.project.simplegw.document.vos.DocumentKind;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferrerRepository extends JpaRepository<Referrer, Long> {
    void deleteByDocsId(Long docsId);
    List<Referrer> findAllByDocsId(Long docsId);

    // 확인하지 않은 결재 참조 문서 카운트
    @Query(value = "select count(a.docs_id) "
                    + "from sgw_referrer a "
                        + "join sgw_document b on a.docs_id = b.id and b.type = 'APPROVAL' and b.registered = '1' "
                    + "where a.referrer_id = :#{#id} and a.checked_datetime is null"
        , nativeQuery = true)
    long countReceivedReferenceDocs(@Param("id") Long referrerId);


    // 참조로 받은 문서 리스트 : 미확인 리스트
    @Query(value = "select c.kind, a.docs_id, c.writer_job_title, c.writer_name, c.title, b.status, approverJobTitle = b.job_title, approverName = b.name, c.created_date "
                    + "from sgw_referrer a "
                        + "join sgw_approval_docs_status b on a.docs_id = b.docs_id "
                        + "join sgw_document c on a.docs_id = c.id and c.type = 'APPROVAL' and c.kind = case when :#{#kind.name()} = 'ALL' then c.kind else :#{#kind.name()} end "
                            + "and c.registered = '1'"
                    + "where a.referrer_id = :#{#id} and a.checked_datetime is null",
        nativeQuery = true)
    List<Object[]> findAllReceivedReferenceDocs(@Param("id") Long referrerId, @Param("kind") DocumentKind kind);


    // 참조로 받은 문서 리스트 : 기간 조회
    @Query(value = "select c.kind, a.docs_id, c.writer_job_title, c.writer_name, c.title, b.status, approverJobTitle = b.job_title, approverName = b.name, c.created_date "
                    + "from sgw_referrer a "
                        + "join sgw_approval_docs_status b on a.docs_id = b.docs_id "
                        + "join sgw_document c on a.docs_id = c.id and c.type = 'APPROVAL' and c.kind = case when :#{#kind.name()} = 'ALL' then c.kind else :#{#kind.name()} end "
                            + "and c.registered = '1'"
                    + "where a.referrer_id = :#{#id} and c.created_date between :#{#start} and :#{#end}",
        nativeQuery = true)
    List<Object[]> findAllReceivedReferenceDocs(@Param("id") Long referrerId, @Param("kind") DocumentKind kind, @Param("start") LocalDate dateStart, @Param("end") LocalDate dateEnd);
}
