package com.project.simplegw.system.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.SseDataType;
import com.project.simplegw.system.vos.SseDisconnType;

import org.springframework.http.MediaType;
// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseService {
    private static final Map<Long, SseEmitter> STORAGE = new ConcurrentHashMap<>(Constants.SSE_EMITTER_STORAGE_CAPACITY);

    public SseService() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }


    private void sendImmediately(SseEmitter sseEmitter, Map<String, Object> data) {
        try {
            sseEmitter.send(data, MediaType.APPLICATION_JSON);
        
        } catch(IOException e) {
            log.warn("{}", e.getMessage());
            log.warn("sseEmitter send IOException.{}", System.lineSeparator());
            log.warn("data: {}", data.toString());
            sseEmitter.completeWithError(e);

        } catch(Exception e) {
            log.warn("{}", e.getMessage());
            log.warn("sseEmitter send Exception.{}", System.lineSeparator());
            log.warn("data: {}", data.toString());
            sseEmitter.completeWithError(e);
        }
    }

    private void send(SseEmitter sseEmitter, Map<String, Object> data) {
        // 즉시 알림을 전달하면 업데이트된 내용을 받는게 아니라 캐시된 내용을 받는 경우가 있어서 쓰레드를 잠깐 지연시킨다.
        try {
            Thread.sleep(50L);
            sendImmediately(sseEmitter, data);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("Thread sleep exception before sseEmitter data send.{}", System.lineSeparator());
        }
    }

    private void remove(LoginUser loginUser, SseDisconnType type) {
        Long memberId = loginUser.getMember().getId();
        SseEmitter sseEmitter = STORAGE.get(memberId);
        
        if(sseEmitter != null)
            sseEmitter.complete();
        
        STORAGE.remove(memberId);
        log.info("user {} server sent event disconnected. type: {}", loginUser.getMember().getId(), type.name());
    }

    



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- SseController ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public SseEmitter connect(LoginUser loginUser) {
        Long memberId = loginUser.getMember().getId();
        SseEmitter sseEmitter = STORAGE.get(memberId);
        log.info("member({}) server sent event connected.", memberId);

        if(sseEmitter == null) {
            sseEmitter = new SseEmitter(Constants.SSE_EMITTER_TIME_OUT);
            STORAGE.put(memberId, sseEmitter);

            sseEmitter.onTimeout(() -> remove(loginUser, SseDisconnType.TIMEOUT));   // 타임아웃된 상태에서 클라이언트가 EventSource를 호출하는 페이지에 있으면 브라우저가 자동으로 다시 연결하게 된다.
            sseEmitter.onError((e) -> remove(loginUser, SseDisconnType.ERROR));   // EventSource 호출하는 페이지를 벗어난 유저의 경우 SseEmitter를 수신하지 못한다. 이 때 에러가 발생할 수 있음.
        }

        // 503 Service Unavailable 방지를 위해 데이터 하나를 전송한다.
        send(sseEmitter, Map.of("SseConnection", "succeed"));

        return sseEmitter;
    }

    public void disconnect(LoginUser loginUser) {
        remove(loginUser, SseDisconnType.CONN_CLOSE);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- SseController ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package sevices ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // @Async   // 호출하는 곳에서 비동기로 호출.
    void sendToAll(SseDataType type) {
        STORAGE.values().forEach(e -> send(e, Map.of(type.name(), "1")));
    }


    // @Async   // 호출하는 곳에서 비동기로 호출.
    public void send(Long memberId, Map<String, Object> data) {
        if(memberId == null)
            return;

        SseEmitter sseEmitter = STORAGE.get(memberId);
        
        if(sseEmitter != null)
            send(sseEmitter, data);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package sevices ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Scheduling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    void monitoring() {
        log.info("SSE object count: {}, users: {}", STORAGE.keySet().size(), STORAGE.keySet().toString());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Scheduling ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
