package com.project.simplegw.document.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.entities.Document;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsRepository extends JpaRepository<Document, Long> {
    List<Document> findByTypeAndKindAndCreatedDateBetweenAndRegisteredOrderByIdDesc(DocumentType type, DocumentKind kind, LocalDate dateStart, LocalDate dateEnd, boolean registered);
    List<Document> findByMemberIdAndTypeAndKindAndCreatedDateBetweenAndRegisteredOrderByIdDesc(Long memberId, DocumentType type, DocumentKind kind, LocalDate dateStart, LocalDate dateEnd, boolean registered);
    
    Document getByIdAndTypeAndKind(Long id, DocumentType type, DocumentKind kind);

    // joincolumn의 파라미터를 이용한 메서드 작성시 파라미터 이름 + 레퍼런스 컬럼 값
    // MemberDetails member;
    // referencedColumnName = "id"
    // --> memberId 인데 맨 앞이 대문자여야 하므로 MemberId
    List<Document> findByMemberIdAndTypeAndKindAndCreatedDateBetweenOrderByIdDesc(Long memberId, DocumentType type, DocumentKind kind, LocalDate dateStart, LocalDate dateEnd);

    List<Document> findByCreatedDateBetweenOrderByIdDesc(LocalDate dateStart, LocalDate dateEnd);   // 기간으로만 조회

    // top 20 개 : 메인화면에 띄워줄 공지사항 리스트. top 20 하는 이유는 게시종료일 설정된 문서로 빠지는 것들을 고려함.
    List<Document> findTop20ByTypeAndKindAndRegisteredOrderByIdDesc(DocumentType type, DocumentKind kind, boolean registered);
    List<Document> findTop5ByTypeAndKindAndRegisteredOrderByIdDesc(DocumentType type, DocumentKind kind, boolean registered);

    // 네이티브 쿼리를 사용하려면 리턴하는 entity에 포함된 모든 필드를 다 써줘야 에러가 나지 않는다.
    // 기간 내 결재요청 받은 전체문서 리스트
    @Query(value = "select b.id, b.type, b.kind, b.title, b.member_id, b.writer_team, b.writer_name, b.writer_job_title, b.created_date, b.created_time, b.updated_datetime " +
                    "from sgw_approval_line a " +
                        "join sgw_document b on a.docs_id = b.id and b.created_date between :#{#start} and :#{#end} and b.type = 'approval' and b.kind = :#{#kind.name()} " +
                    "where a.member_id = :#{#id}",
        nativeQuery = true)
    List<Document> querySearchApprovalDocsByCreatedDateAndKindAndMemberId(@Param("start") LocalDate dateStart, @Param("end") LocalDate dateEnd, @Param("kind") DocumentKind kind, @Param("id") Long memberId);

    long countByMemberIdAndRegistered(Long writerId, boolean registered);

    List<Document> findAllByMemberIdAndRegisteredOrderByIdDesc(Long memberId, boolean registered);


    // 회의록 리스트: 내가 작성한 것 + 공유로 받은 것
    @Query(value = "select a.id, a.member_id, a.type, a.kind, a.title, a.writer_name, a.writer_team, a.writer_job_title, a.created_date, a.created_time, a.updated_datetime, a.registered " +
                    "from sgw_document a " +
                    "where a.member_id = :#{#id} and a.type = 'BOARD' and a.kind = 'MEETING' and a.created_date between :#{#start} and :#{#end} and a.registered = '1' " +
                    
                    "union all " +

                    "select a.id, a.member_id, a.type, a.kind, a.title, a.writer_name, a.writer_team, a.writer_job_title, a.created_date, a.created_time, a.updated_datetime, a.registered " +
                    "from sgw_document a " +
                        "join sgw_referrer b on a.id = b.docs_id and b.referrer_id = :#{#id} " +
                    "where a.type = 'BOARD' and a.kind = 'MEETING' and a.created_date between :#{#start} and :#{#end} and a.registered = '1' ",
        nativeQuery = true)
    List<Document> findAllMeetingMinutes(@Param("id") Long memberId, @Param("start") LocalDate dateStart, @Param("end") LocalDate dateEnd);
}
