package com.project.simplegw.system.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simplegw.common.vos.SseData;
import com.project.simplegw.document.vos.DocumentKind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Long, SseEmitter> sseStorage = new ConcurrentHashMap<>();
    // private static final Long EMITTER_TIME_OUT = 1000L * 5;   // 5 seconds test
    private static final Long EMITTER_TIME_OUT = 1000L * 60 * 15;   // 15 minutes

    public SseService() {
        logger.info("Server Sent Event 서비스를 시작합니다.");
    }

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = sseStorage.get(memberId);
        if(emitter == null) {
            emitter = new SseEmitter(EMITTER_TIME_OUT);
            sseStorage.put(memberId, emitter);
            logger.info("memberId {} 의 SseEmitter 인스턴스를 생성하였습니다.", memberId);
            logger.info("sseStorage에 저장된 emitter count: {}", sseStorage.size());

            // 타임아웃되면 emitter 객체를 종료하고 저장소에서 삭제. 만약 유저가 EventSource를 호출하는 페이지를 유지하고 있다면 js의 EventSource 함수에서 자동으로 다시 연결한다.
            emitter.onTimeout(() -> {
                // sseStorage.get(memberId).complete();   // timeout되면 인스턴스가 알아서 제거된다. 로컬 테스트와 달리 운영에서 NPE가 뜨고 있어서 주석처리.
                sseStorage.remove(memberId);
                logger.info("memberId {} 의 SseEmitter 인스턴스가 타임아웃되어 종료됩니다. 인스턴스를 제거합니다.", memberId);
                logger.info("sseStorage에 저장된 emitter count: {}", sseStorage.size());
            });

            // sse 연결이 되어 있으나 데이터를 수신하지 못하는 상태에 있는 유저도 있으므로 onError에서 제거한다.
            // 수신하지 못하는 상태: EventSource 함수로 subscribe했던 js파일이 포함된 페이지에서 벗어난 유저
            emitter.onError(throwable -> {
                sseStorage.remove(memberId);
                sseStorage.get(memberId).completeWithError(throwable);
                logger.info("memberId {} 의 SSE 수신이 불가하여 인스턴스를 제거합니다.", memberId);
                logger.info("sseStorage에 저장된 emitter count: {}", sseStorage.size());
            });
        }

        Map<String, String> data = new HashMap<>();
        data.put("SseConnection", "ok");
        send(emitter, data);   // 503 Service Unavailable 방지를 위해 데이터 아무거나 하나를 전송한다.
        
        return emitter;
    }

    @Async
    public void deleteSseEmitter(Long memberId) {
        SseEmitter emitter = sseStorage.get(memberId);
        if(emitter != null) {
            emitter.complete();
            sseStorage.remove(memberId);
            logger.info("Event 수신 상태를 벗어난 memberId {} 의 SseEmitter 인스턴스를 제거합니다.", memberId);
            logger.info("sseStorage에 저장된 emitter count: {}", sseStorage.size());
        }
    }

    private String convertJsonString(Map<String, String> data) {
        ObjectMapper ojbMapper = new ObjectMapper();
        String result = null;
        try {
            result = ojbMapper.writeValueAsString(data);

        } catch(Exception e) {
            result = "sse data를 json string으로 변환하는 중 오류가 발생하였습니다.";
            e.printStackTrace();
            logger.warn("{}{}{}", e.getMessage(), System.lineSeparator(), result);
        }
        return result;
    }

    private void send(SseEmitter emitter, Map<String, String> data) {
        try {
            emitter.send(convertJsonString(data));
        } catch(Exception e) {
            e.printStackTrace();
            logger.info("{}{}Server Sent Event 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
            emitter.completeWithError(e);
        }
    }

    @Async
    public void sendToClients(SseData type) {   // 현재 등록된 모든 클라이언트에게 전달.
        sseStorage.values().stream().forEach(emitter -> {   // 이벤트 발생 시 현재 연결된 모든 유저들에게 데이터 전달.
            Map<String, String> data = new HashMap<>();
            data.put(type.name(), "true");
            send(emitter, data);
        });
    }

    @Async   // 결재 문서 등록자에게 전달
    public void sendToSubmitter(Long memberId, SseData type, Long docsId, DocumentKind kind, String title) {
        SseEmitter target = sseStorage.get(memberId);
        if(target != null) {
            switch(type) {
                case CONFIRMED:
                case REJECTED:
                    Map<String, String> data = new HashMap<>();
                    data.put(type.name(), "true");
                    data.put("docsId", Long.toString(docsId));
                    data.put("kind", kind.getTitle());
                    data.put("title", title);

                    send(target, data);
                    return;

                default:
                    return;
            }
        }
    }

    @Async   // 결재 순서에 해당하는 결재자 또는 참조자들에게 전달할 때 사용
    public void sendToClientForApproval(Long memberId, SseData type) {   // 특정 클라이언트만 전달.
        SseEmitter target = sseStorage.get(memberId);
        if(target != null) {
            switch(type) {
                case APPROVER:
                case REFERRER:
                    Map<String, String> data = new HashMap<>();
                    data.put(type.name(), "true");
                    send(target, data);
                    return;

                default:
                    return;
            }
        }
    }

    @Async
    public void sendAlarm(Long memberId) {
        SseEmitter target = sseStorage.get(memberId);
        if(target != null) {
            Map<String, String> data = new HashMap<>();
            data.put(SseData.ALARM.name(), "true");
            send(target, data);
        }
    }
}