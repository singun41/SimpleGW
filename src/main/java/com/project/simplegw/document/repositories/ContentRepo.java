package com.project.simplegw.document.repositories;

import java.util.Optional;

import com.project.simplegw.document.entities.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContentRepo extends JpaRepository<Content, Long> {
    //  Optional<Content> findByDocuId(Long docuId);
    // 단순히 docu id만 이용해 검색하게 되면 노출된 url을 이용해 id만으로 모든 문서 내용을 볼 수 있기 때문에 디테일한 조건을 추가한다.
    // @Query(value =
    //     "select a.id, a.docs_id, a.content, a.created_datetime, a.updated_datetime " +
    //     "from docs_content a " +
    //     "join docs b on a.docs_id = b.id and b.id = :#{#docsId} and b.type = :#{#type.name()}",
    // nativeQuery = true)
    // Optional<Content> findByDocsIdAndType(@Param("docsId") Long docsId, @Param("type") DocsType type);

    // type 체크는 DocsService 클래스에서 처리.
    Optional<Content> findByDocsId(Long docsId);
}
