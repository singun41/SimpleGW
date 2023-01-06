package com.project.simplegw.document.repositories;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.vos.DocsType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsRepo extends JpaRepository<Docs, Long> {
    // created_date between '' and '' and type = '' 으로 쿼리를 실행해보면 옵티마이저가 type을 선순위로 변경한다.
    // 따라서 index도 그에 맞게 타게 되므로, 필드 순서를 옵티마이저가 처리하는 순서로 변경한다.
    List<Docs> findByTypeAndCreatedDateBetweenOrderByIdDesc(DocsType type, LocalDate dateFrom, LocalDate dateTo);
    
    // 최신 20개 가져오기
    // 메인화면의 최근 공지사항 게시 리스트를 보여주기 위함.
    // DocsOptions에 의해서 게시 숨김 처리되는 경우도 있기 때문에 20개 정도 가져와서 처리.
    List<Docs> findTop20ByTypeOrderByIdDesc(DocsType type);


    // ID로만 가져오게 될 경우 어느 문서든지 접근이 가능하므로, Type까지 체크해서 정확한 문서만 찾는다.
    // type 체크는 서비스에서 content 엔티티에서 getDocs()로 받은 뒤 체크하도록 변경.
    // Optional<Docs> findByIdAndType(Long id, DocsType type);
}
