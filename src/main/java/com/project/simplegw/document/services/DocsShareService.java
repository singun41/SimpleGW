package com.project.simplegw.document.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.approval.entities.Referrer;
import com.project.simplegw.approval.repositories.ReferrerRepository;
import com.project.simplegw.document.dtos.DocsShareDTO;
import com.project.simplegw.document.entities.Document;
import com.project.simplegw.member.services.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class DocsShareService {
    
    private final ReferrerRepository referrerRepo;
    private final MemberService memberService;
    private final DocsService docsService;

    @Autowired
    public DocsShareService(ReferrerRepository referrerRepo, MemberService memberService, DocsService docsService) {
        this.referrerRepo = referrerRepo;
        this.memberService = memberService;
        this.docsService = docsService;
    }

    @Async
    public void saveReferrer(DocsShareDTO dto) {
        Long docsId = dto.getDocsId();
        Document docs = docsService.findById(docsId);
        Long writerId = docs.getMember().getId();
        
        List<Long> referrers = dto.getReferrers();
        List<Long> savedReferrers = referrerRepo.findAllByDocsId(docsId).stream().mapToLong(referrer -> referrer.getReferrer().getId()).boxed().collect(Collectors.toList());

        // 문서 작성자는 제거한다.
        referrers.remove(writerId);

        // 중복 제거
        referrers.removeAll(savedReferrers);

        // 공유(참조자) 저장
        referrerRepo.saveAll( referrers.stream().map(refId -> Referrer.builder().docs(docs).build().insertReferrer(memberService.searchMemberDetailsById(refId))).collect(Collectors.toList()) );
    }

    @Async   // 참조자로 설정된 경우 페이지를 열 때 확인시간을 업데이트한다.
    public void updateReferrerChecked(Long docsId, Long referrerId) {
        List<Referrer> referrers = referrerRepo.findAllByDocsId(docsId);
        if(referrers == null) return;

        Optional<Referrer> result = referrers.stream().filter(elem -> elem.getReferrer().getId().equals(referrerId)).findFirst();
        if(result.isPresent()) {
            Referrer referrer = result.get();

            if(referrer.getCheckedDatetime() == null) {   // 데이터가 없는 경우 최초 1번만 업데이트한다.
                referrerRepo.save(referrer.updateCheckedDatetime());
            }
        }
    }
}
