package com.project.simplegw.system.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.services.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String defaultSuccessUrl;
    private final MemberService memberService;
    
    public CustomAuthSuccessHandler(String defaultSuccessUrl, MemberService memberService) {
        this.defaultSuccessUrl = defaultSuccessUrl;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String ipAddr = request.getHeader("X-FORWARDED-FOR");
        if(ipAddr == null || ipAddr.isBlank()) {
            ipAddr = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddr == null || ipAddr.isBlank()) {
            ipAddr = request.getRemoteAddr();
        }

        String browser = null;
        String userAgent = request.getHeader("User-Agent");
        String device = null;

        // browser check
        if(userAgent.contains("Trident")) { // IE
            browser = "IE";
        } else if(userAgent.contains("Edge") || userAgent.contains("Edg")) { // Edge
            browser = "Edge";
        } else if(userAgent.contains("Whale")) { // Naver Whale
            browser = "Naver Whale";
        } else if(userAgent.contains("Opera") || userAgent.contains("OPR")) { // Opera
            browser = "Opera";
        } else if(userAgent.contains("Firefox")) { // Firefox
            browser = "Firefox";
        } else if(userAgent.contains("Chrome")) { // Chrome
            browser = "Chrome";
        } else if(userAgent.contains("Safari")) { // Safari
            browser = "Safari";
        } else {
            browser = "Others";
        }

        // device check
        if(userAgent.toUpperCase().indexOf("MOBI") > -1) {
            device = "Mobile";
        } else {
            device = "PC";
        }

        SecurityUser loginUser = (SecurityUser)authentication.getPrincipal();

        MemberDetails memberDetails = memberService.searchMemberDetailsById(loginUser.getMember().getId());
        logger.info("Logged in user: {}, {}, {}, {}, Host ip: {}, Browser: {}, Device: {}, User-Agent: {}",
                    memberDetails.getId(), memberDetails.getTeam(), memberDetails.getName(), memberDetails.getJobTitle(),
                    ipAddr, browser, device, userAgent);
        memberService.clearMemberFailureCount(memberDetails.getMember().getUserId());
        response.sendRedirect(defaultSuccessUrl);
    }
}