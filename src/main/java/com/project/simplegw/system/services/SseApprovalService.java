package com.project.simplegw.system.services;

import java.util.Collection;
import java.util.Map;

import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.system.vos.SseApprovalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseApprovalService {
    private final SseService sseService;
    private final NotificationService notiService;

    @Autowired
    public SseApprovalService(SseService sseService, NotificationService notiService) {
        this.sseService = sseService;
        this.notiService = notiService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }
    

    public void sendToApprovalSubmitter(SseApprovalType type, Docs docs) {
        if(docs.getWriterId() == null)
            return;
        
        switch(type) {
            case CONFIRMED, REJECTED -> {
                String content =
                    new StringBuilder("결재문서 ").append(docs.getTitle())
                        .append(" (")
                        .append(docs.getType().getTitle())
                        .append(", No. ").append(docs.getId())
                        .append(") 의 결재가 ")
                        .append(
                            type == SseApprovalType.CONFIRMED ? "<strong class='text-success'>승인" : "<strong class='text-danger'>반려"
                        ).append("</strong>")
                        .append(" 되었습니다.")
                    .toString();

                notiService.create( docs.getWriterId(), content );

                sseService.send(
                    docs.getWriterId(),
                    Map.of(
                        type.name(), "1",
                        "content", content
                    )
                );
            }
            default -> {}
        }
    }


    public void sendToCurrentApprover(Long memberId) {
        if(memberId == null)
            return;

        sseService.send(memberId, Map.of(SseApprovalType.APPROVER.name(), "1"));
    }


    public void sendToAllReferrers(Collection<Long> referrers) {
        if(referrers == null)
            return;
        
        referrers.forEach(e -> sseService.send(e, Map.of(SseApprovalType.REFERRER.name(), "1")));
    }


    public void sendToReferrer(Long memberId) {
        if(memberId == null)
            return;

        sseService.send(memberId, Map.of(SseApprovalType.REFERRER.name(), "1"));
    }
}
