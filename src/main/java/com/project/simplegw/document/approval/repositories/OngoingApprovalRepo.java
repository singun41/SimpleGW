package com.project.simplegw.document.approval.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.simplegw.document.approval.entities.OngoingApproval;

@Repository
public interface OngoingApprovalRepo extends JpaRepository<OngoingApproval, Long> {
    Optional<OngoingApproval> findByDocsId(Long docsId);
    List<OngoingApproval> findByOwnerId(Long ownerId);
    List<OngoingApproval> findByApproverId(Long approverId);

    long countByOwnerId(Long ownerId);         // 결재 진행중인 문서 카운트
    long countByApproverId(long approverId);   // 결재요청 받은 문서 카운트 (참조요청 받은 문서 카운트는 ReferrerRepo에서..)

    void deleteByDocsId(Long docsId);          // 완료된 문서는 삭제한다.
}
