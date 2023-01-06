package com.project.simplegw.system.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.system.vos.Constants;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AjaxAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    // ajax로 통신할 때 세션 만료를 알리기 위해 커스터마이징 필터 작성 및 SecurityConfig에 추가
    public AjaxAuthenticationEntryPoint() {
        super(Constants.DEFAULT_LOGIN_URL);
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String ajaxHeader = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);   // ajaxHeader.eqauls("XMLHttpRequest"); 로 바꾸면 NPE 발생.
        
        if(isAjax)
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ajax Request Denied (Session Expired)");
        else
            super.commence(request, response, authException);
    }
}
