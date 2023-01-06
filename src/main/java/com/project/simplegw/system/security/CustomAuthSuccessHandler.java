package com.project.simplegw.system.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.services.MemberLoginService;
import com.project.simplegw.system.vos.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    
    private final MemberLoginService memberLoginService;
    private final CustomAuthFailureHandler failureHandler;

    @Autowired
    public CustomAuthSuccessHandler(MemberLoginService memberLoginService, CustomAuthFailureHandler failureHandler) {
        this.memberLoginService = memberLoginService;
        this.failureHandler = failureHandler;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
        String ipAddr = req.getHeader("X-FORWARDED-FOR");
        
        if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getHeader("Proxy-Client-IP");
        if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getRemoteAddr();


        String browser = null;
        String userAgent = req.getHeader(Constants.USER_AGENT);
        boolean isMobile = false;


        if(userAgent.contains("Trident"))   // IE
            browser = "IE";

        else if(userAgent.contains("Edg"))   // Edge
            browser = "Edge";

        else if(userAgent.contains("Whale"))   // Naver Whale
            browser = "Naver Whale";

        else if(userAgent.contains("Opera") || userAgent.contains("OPR"))   // Opera
            browser = "Opera";

        else if(userAgent.contains("Firefox"))   // Firefox
            browser = "Firefox";

        else if(userAgent.contains("Chrome"))   // Chrome
            browser = "Chrome";

        else if(userAgent.contains("Safari"))   // Safari
            browser = "Safari";

        else
            browser = "Others";
        
        if(userAgent.toUpperCase().contains(Constants.MOBILE_CHECK_STR))
            isMobile = true;
        
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        MemberData userInfo = memberLoginService.getMemberData(loginUser);

        log.info(
            "Logged in user - ID: {}, {} {} {}, Host ip: {}, Browser: {}, Device: {}, User-Agent: {}",
            userInfo.getId(), userInfo.getTeam(), userInfo.getJobTitle(), userInfo.getName(), ipAddr, browser, isMobile ? "Mobile" : "PC", userAgent
        );
        
        failureHandler.clearFailureCount(loginUser.getUsername());
        res.sendRedirect(Constants.DEFAULT_MAIN_URL);   // ViewController
    }
}
