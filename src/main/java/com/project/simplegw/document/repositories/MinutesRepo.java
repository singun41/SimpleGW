package com.project.simplegw.document.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;

@Repository
public interface MinutesRepo extends JpaRepository<Docs, Long> {
    // 회의록 리스트: 내가 작성한 것 or 공유받은 것
    @Query(
        value = """
                    select
                        a.id,
                        a.[type], a.title,
                        a.writer_id, a.writer_team, a.writer_job_title, a.writer_name,
                        a.created_date, a.created_time, a.updated_datetime
                    from docs a
                    where 1=1
                        and a.writer_id = :#{#member_id}
                        and a.[type] = :#{#type.name()}
                        and a.created_date between :#{#date_from} and :#{#date_to}

                    union

                    select
                        a.id,
                        a.[type], a.title,
                        a.writer_id, a.writer_team, a.writer_job_title, a.writer_name,
                        a.created_date, a.created_time, a.updated_datetime
                    from docs a
                        join referrer b on a.id = b.docs_id
                    where 1=1
                        and b.member_id = :#{#member_id}
                        and a.[type] = :#{#type.name()}
                        and a.created_date between :#{#date_from} and :#{#date_to}

                    order by a.id desc
                """,
        nativeQuery = true
    )
    List<Docs> findList(@Param("member_id") Long memberId, @Param("type") DocsType type, @Param("date_from") LocalDate dateFrom, @Param("date_to") LocalDate dateTo);


    // 관리자 권한 검색일 때: 작성자, 공유된 것 관계없이 기간으로 모두 조회.
    List<Docs> findByTypeAndCreatedDateBetween(DocsType type, LocalDate dateFrom, LocalDate dateTo);
}
