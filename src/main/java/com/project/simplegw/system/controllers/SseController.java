package com.project.simplegw.system.controllers;

import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.SseService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {   // Server Sent Event Controller
    private final SseService sseService;

    @Autowired
    public SseController(SseService sseService) {
        this.sseService = sseService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(HttpServletResponse res, @AuthenticationPrincipal LoginUser loginUser) {
        /*
            많은 레퍼런스들이 HttpServletResponse 셋팅없이 리턴하는 코드로 되어 있다.
            응답헤더를 별도 세팅하지 않고 SseEmitter를 리턴해도 로컬 테스트할 때는 문제없이 동작한다.

            그러나 TLS를 적용한 웹브라우저에서는 비정상적인 끊김 현상이 발생한다.
            Tomcat async-timeout, keep-alive 설정을 변경해도, SseEmitter timeout을 늘리거나 줄여도 마찬가지이다.

            해결방법: 응답헤더에 Connection=keep-alive, Keep-alive=시간(초), X-Accel-Buffering=no 속성을 추가해주면 TLS가 적용된 경우에도 잘 동작하게 된다.
            
            - 주의점
            너무 잦은 데이터 전송은 연결끊김 에러가 발생한다.
            15초 간격으로 데이터를 전달할 때에도 발생하였으니 반드시 필요한 경우에만 데이터를 전송하도록 설계해야 한다.
        */
        res.addHeader("Connection", "keep-alive");
        res.addHeader("Keep-alive", "timeout=900");   // 여기 timeout 시간은 sseEmitter timeout 시간보다 길게 잡는다.
        res.addHeader("X-Accel-Buffering", "no");
        return sseService.connect(loginUser);
    }

    @GetMapping("/disconnect")
    public void disconnect(@AuthenticationPrincipal LoginUser loginUser) {
        sseService.disconnect(loginUser);
    }
}
