package com.project.simplegw.system.security;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.simplegw.member.repositories.MemberRepo;
import com.project.simplegw.system.vos.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
	
	// 로그인 실패시 카운트 집계용.
	private final Map<String, Integer> failureCountStorage = new ConcurrentHashMap<>();
	private final MemberRepo memberRepo;

	@Autowired
	public CustomAuthFailureHandler(MemberRepo memberRepo) {
		this.memberRepo = memberRepo;
		log.info("Component '" + this.getClass().getName() + "' has been created.");
	}

	
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException authEx) throws IOException, ServletException {
		StringBuilder errorMsg = new StringBuilder();
		String userId = req.getParameter(Constants.USERNAME_PARAM);
		String ipAddr = req.getHeader("X-FORWARDED-FOR");

		if(ipAddr == null || ipAddr.isBlank())
            ipAddr = req.getHeader("Proxy-Client-IP");
		if(ipAddr == null || ipAddr.isBlank())
			ipAddr = req.getRemoteAddr();


		// 패스워드가 틀렸다는 메시지를 따로 전달하게 되면 ID가 일치한다는 것을 명시하는 것이므로, ID나 패스워드가 틀렸다는 통합 문구로 안내한다.
		if(authEx instanceof BadCredentialsException || authEx instanceof InternalAuthenticationServiceException) {
			int cnt = increaseFailureCount(userId);

			if(cnt < 5) {
				errorMsg.append("로그인 실패");
				log.warn("User login failed. ID or password mismatched. login failed userId: {}, host ip: {}", userId, ipAddr);

			} else {
				errorMsg.append(Constants.LOGIN_FAIL_COUNT_LIMIT).append("번 실패하여 사용 중지되었습니다.");
				log.warn("User login failed. ID is disabled. login failed userId: {}, host ip: {}", userId, ipAddr);
			}

		} else if(authEx instanceof DisabledException) {
			errorMsg.append("사용 중지된 ID 입니다.");
			log.warn("User's access is denied. login failed. login failed userId: {}, host ip: {}", userId, ipAddr);

		} else {
			errorMsg.append("시스템 에러입니다.");
			log.warn("Login error. login failed userId: {}, host ip: {}", userId, ipAddr);
		}

		req.setAttribute("errorMsg", errorMsg.toString());
		req.getRequestDispatcher(Constants.DEFAULT_LOGIN_URL).forward(req, res);   // SystemController
	}



	private int increaseFailureCount(String userId) {
		failureCountStorage.putIfAbsent(userId, 0);
		int cnt = failureCountStorage.computeIfPresent(userId, (k, v) -> v + 1);
		log.info("failure cnt: {}", cnt);

		if(cnt > Constants.LOGIN_FAIL_COUNT_LIMIT) {
			log.info("failure count exceeded. account disabled");
			memberRepo.findByUserId(userId).ifPresent(member -> {
				memberRepo.save(member.updateEnabled(false));
				failureCountStorage.computeIfPresent(userId, (k, v) -> null);   // null 값을 전달하면 제거된다.
			});
		}
		return cnt;
	}


	public void clearFailureCount(String userId) {
		failureCountStorage.computeIfPresent(userId, (k, v) -> null);   // null 값을 전달하면 제거된다.
	}


	public void clearAll() {
		failureCountStorage.clear();
	}
}
