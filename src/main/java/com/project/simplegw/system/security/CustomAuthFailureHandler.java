package com.project.simplegw.system.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.member.services.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String defaultFailureUrl;
	private final MemberService memberService;
	
	public CustomAuthFailureHandler(String defaultFailureUrl, MemberService memberService) {
		this.defaultFailureUrl = defaultFailureUrl;
		this.memberService = memberService;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String userId = request.getParameter("userId");
		String ipAddr = request.getHeader("X-FORWARDED-FOR");
		if(ipAddr == null || ipAddr.isBlank()) {
            ipAddr = request.getHeader("Proxy-Client-IP");
        }
		if(ipAddr == null || ipAddr.isBlank()) {
			ipAddr = request.getRemoteAddr();
		}
		StringBuilder errorMsg = new StringBuilder();
		// 패스워드가 틀렸다는 메시지를 따로 전달하게 되면 ID가 일치한다는 것을 명시하는 것이므로, ID나 패스워드가 틀렸다는 통합 문구로 안내한다.
		// --> BruteForce에 취약한 부분을 조금이나마 방어하기 위해
		if(exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
			int count = memberService.getFailureCount(userId);
			errorMsg.append("ID나 비밀번호가 틀렸습니다.").append(System.lineSeparator()).append("실패 횟수: ").append(count);
			logger.warn("User login failed. ID or password mismatched. login failed userId: {}, host ip: {}", userId, ipAddr);
			if(count >= 5) {
				errorMsg.append(System.lineSeparator()).append("실패 횟수가 5번을 초과하여 사용 중지되었습니다.")
					.append(System.lineSeparator()).append("전산팀으로 문의하세요.");
				logger.warn("User login failed. ID is disabled. login failed userId: {}, host ip: {}", userId, ipAddr);
			}
		} else if(exception instanceof DisabledException) {
			errorMsg.append("사용 중지 처리된 ID 입니다.");
			logger.warn("User's access is denied. login failed. login failed userId: {}, host ip: {}", userId, ipAddr);
		} else {
			errorMsg.append("로그인 시스템 에러입니다.");
			logger.warn("Login error. login failed userId: {}, host ip: {}", userId, ipAddr);
		}
		request.setAttribute("errorMsg", errorMsg.toString());
		request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
	}
}