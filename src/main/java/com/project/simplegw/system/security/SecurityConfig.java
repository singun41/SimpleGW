package com.project.simplegw.system.security;


import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.member.vos.MemberRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
// import org.springframework.security.crypto.factory.PasswordEncoderFactories;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SecurityUserDetailsService userDetailsService;
	private final MemberService memberService;

	@Autowired
	public SecurityConfig(SecurityUserDetailsService userDetailsService, MemberService memberService) {
		this.userDetailsService = userDetailsService;
		this.memberService = memberService;
	}

	@Override
	protected void configure(HttpSecurity security) {
		try {
			security
				.authorizeRequests()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()   // 정적 리소스들에 대한 접근 허가를 처리함
				.antMatchers("/robots.txt").permitAll()   // robots.txt에 접근할 수 있게 허용. 웹크롤러를 차단하기 위함.
				.antMatchers("/login").permitAll()   // 로그인, 로그아웃, 세션만료 페이지는 허용
				
				.antMatchers("/admin/**").hasAuthority(MemberRole.ADMIN.name())   // templates 디렉토리 구조가 아닌 Web URL 기준으로 설정(컨트롤러에 설정한 매핑 path 또는 value 기준)
				.antMatchers("/notice/writepage/**").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MANAGER.name(), MemberRole.LEADER.name(), MemberRole.DIRECTOR.name(), MemberRole.MASTER.name())
				.antMatchers("/notice/modifypage/**").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MANAGER.name(), MemberRole.LEADER.name(), MemberRole.DIRECTOR.name(), MemberRole.MASTER.name())
				.antMatchers("/archive/writepage/**").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MANAGER.name(), MemberRole.LEADER.name(), MemberRole.DIRECTOR.name(), MemberRole.MASTER.name())
				.antMatchers("/archive/modifypage/**").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MANAGER.name(), MemberRole.LEADER.name(), MemberRole.DIRECTOR.name(), MemberRole.MASTER.name())
				.antMatchers("/**").authenticated()   // 기본적으로 모든 권한이 있어야 사용 가능 --> 로그인 필요.

			.and()
				.exceptionHandling().accessDeniedPage("/error/403")   // 권한 없는 페이지 접근시 페이지 설정.
				.authenticationEntryPoint(new AjaxAuthenticationEntryPoint("/login"))   // 세션 만료된 상태에서 ajax로 요청시 상태 코드 403 리턴
			
			.and()
				.csrf().disable()
				.cors().disable()

				.formLogin().loginPage("/login").usernameParameter("userId").passwordParameter("userPw")
				.successHandler(new CustomAuthSuccessHandler("/frame", memberService))
				.failureHandler(new CustomAuthFailureHandler("/login", memberService))
			
			.and()
				.userDetailsService(userDetailsService)   // 스프링시큐리티에 유저 정보 전달.

				.sessionManagement().invalidSessionUrl("/login")   // 세션 타임아웃 시 페이지 이동, 로그아웃할 때에도 실행됨. logoutSuccessUrl 메서드가 무시된다.
				.maximumSessions(1)   // 로그인 세션 최대 개수는 1개
				.sessionRegistry(sessionRegistry()).expiredUrl("/login")   // 중복 로그인시 기존 세션은 파기시키고 페이지 이동
				.maxSessionsPreventsLogin(false)   // 중복 로그인 시 기존 사용자 세션 종료.
			
			.and().and()
				.logout()   // 로그아웃 설정
				// .logoutSuccessUrl("/afterLogout")   // 세션 만료시 이동할 페이지 지정, invalidSessionUrl 메서드가 먼저 실행되고 로그인페이지로 이동하므로 이 메서드는 실행 되지 않음.
				.invalidateHttpSession(true).deleteCookies("JSESSIONID").clearAuthentication(true)   // 로그아웃 시 세션제거, 쿠키 제거

			.and()
				.headers().frameOptions().sameOrigin();   // iFrame을 사용할 때 동일 도메인에서만 X-Frame-Options Deny 해제.

		} catch(Exception e) {
			e.printStackTrace();
			logger.warn("{}{}configure 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
		}
	}

	// 객체 순환참조때문에 PwEncoder 클래스로 분리
	// @Bean
	// public PasswordEncoder passwordEncoder() {
	// 	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	// }

	// session 관련 추가. 아래 2개의 bean이 없으면 세션 중복 에러 해결이 안 된다.
	// 세션 중복 에러: A 유저가 로그인 후 루그아웃 -> 다시 로그인할 때 'Maximum sessions of 1 for this principal exceeded' 에러가 난다.
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	} // Register HttpSessionEventPublisher

	@Bean
	public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}

	// cors 설정을 위한 메서드: 타임리프 템플릿을 사용하므로 불필요.
	/*
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	*/
}
