package com.project.simplegw.system.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.vos.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SystemController {
    public SystemController() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    // 크롤러에 의한 검색 방지, text/plain, UTF-8 설정을 해줘야 browser에서 라인피드가 먹는다.
    @GetMapping(value="/robots.txt", produces="text/plain;charset=UTF-8")
    @ResponseBody
    public String robots() { return "User-agent: *" + System.lineSeparator() + "Disallow: /"; }

    @RequestMapping(Constants.DEFAULT_LOGIN_URL)   // 로그인 실패시 메시지를 전달하려면 GetMapping 대신 RequestMapping으로 설정해야 한다.
    public String login(HttpServletRequest req, @AuthenticationPrincipal LoginUser loginUser) {
        boolean loggedIn = loginUser != null ? true : false;

        if(req.getHeader(Constants.USER_AGENT).toUpperCase().contains(Constants.MOBILE_CHECK_STR))
            if(loggedIn)
                return "redirect:m/main";
            else
                return "login/login-mobile";
        
        else
            if(loggedIn)
                return "redirect:main";
            else
                return "login/login";
    }

    @GetMapping("/logout")
    public void logout() { }
    
    @GetMapping("/error/400")
    public void badRequest() { }

    @GetMapping("/error/403")
    public void forbidden() { }

    @GetMapping("/error/403-modify")
    public String fobiddenModify() { return Constants.ERROR_PAGE_403_MODIFY; }
    
    @GetMapping("/error/404")
    public void notFound() { }

    @GetMapping("/error/405")
    public void methodNotAllowed() { }

    @GetMapping("/error/500")
    public void internalServerError() { }
    
    @GetMapping("/sessionExpired")
    public String sessionExpired() { return "sessionExpired"; }
}
