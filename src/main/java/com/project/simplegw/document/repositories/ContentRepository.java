package com.project.simplegw.document.repositories;

import java.util.Optional;

import com.project.simplegw.document.entities.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    //  Optional<Content> findByDocuId(Long docuId);
    // 단순히 docu id만 이용해 검색하게 되면 노출된 url을 이용해 id만으로 모든 문서 내용을 볼 수 있기 때문에 디테일한 조건을 추가한다.
    @Query(value = "select a.id, a.docs_id, a.content, a.created_datetime, a.updated_datetime "
                    + "from sgw_docs_content a "
                        + "join sgw_document b on a.docs_id = b.id and b.id = ?1 and b.type = ?2 and b.kind = ?3"
        , nativeQuery = true)
    Optional<Content> findByDocsIdAndTypeAndKind(Long docsId, String type, String kind);
}
