package com.project.simplegw.system.security;

import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final CustomAuthSuccessHandler customAuthSuccessHandler;
	private final CustomAuthFailureHandler customAuthFailureHandler;
	private final AjaxAuthenticationEntryPoint ajaxAuthEntryPoint;

	@Autowired
	public SecurityConfig(
		UserDetailsServiceImpl userDetailsService, AjaxAuthenticationEntryPoint ajaxAuthEntryPoint,
		CustomAuthSuccessHandler customAuthSuccessHandler, CustomAuthFailureHandler customAuthFailureHandler
	) {
		this.userDetailsService = userDetailsService;
		this.customAuthSuccessHandler = customAuthSuccessHandler;
		this.customAuthFailureHandler = customAuthFailureHandler;
		this.ajaxAuthEntryPoint = ajaxAuthEntryPoint;

		log.info("Configuration '" + this.getClass().getName() + "' has been created.");
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		log.info(this.getClass().getName() + "'s mothod 'filterChain' called.");
		
		http
			.authorizeRequests()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()   // 정적 리소스들에 대한 접근 허가를 처리함
			.antMatchers(Constants.DEFAULT_LOGIN_URL, Constants.MOBILE_LOGIN_URL).permitAll()   // 로그인, 로그아웃, 세션만료 페이지는 허용

			.antMatchers("/robots.txt").permitAll()   // robots.txt에 접근할 수 있게 허용. 웹크롤러를 차단하기 위함.
		
			.antMatchers("/page/admin/**", "/page/m/admin/**").hasAuthority(Role.ADMIN.name())   // templates 디렉토리 구조가 아닌 Web URL 기준으로 설정(컨트롤러에 설정한 매핑 path 또는 value 기준)
			
			// MenuAthority를 사용해서 설정한다.
			// .antMatchers("/page/notice/write/**").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name(), Role.LEADER.name(), Role.DIRECTOR.name(), Role.MASTER.name())
			// .antMatchers("/page/notice/modify/**").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name(), Role.LEADER.name(), Role.DIRECTOR.name(), Role.MASTER.name())
			// .antMatchers("/page/archive/write/**").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name(), Role.LEADER.name(), Role.DIRECTOR.name(), Role.MASTER.name())
			// .antMatchers("/page/archive/modify/**").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name(), Role.LEADER.name(), Role.DIRECTOR.name(), Role.MASTER.name())
			
			.anyRequest().authenticated()   // 기본적으로 모든 권한이 있어야 사용 가능 --> 로그인 필요.

		.and()
			.csrf().disable()
			.cors().disable()
			.httpBasic()   // form 인증 방식을 사용.

		.and()
			.formLogin().loginPage(Constants.DEFAULT_LOGIN_URL).loginProcessingUrl(Constants.DEFAULT_LOGIN_URL)
			.usernameParameter(Constants.USERNAME_PARAM).passwordParameter(Constants.PASSWORD_PARAM)
			.successHandler(customAuthSuccessHandler).failureHandler(customAuthFailureHandler)
		
		.and()
			.userDetailsService(userDetailsService)   // 스프링시큐리티에 유저 정보 전달.

			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   // 기본값. 로그인 이후에 세션이 생성되어야 하므로.
			.invalidSessionUrl(Constants.DEFAULT_LOGIN_URL)   // 세션 타임아웃 시 페이지 이동, 로그아웃할 때에도 실행됨. logoutSuccessUrl 메서드가 무시된다.
			.maximumSessions(1)   // 유저별 로그인 세션 최대 개수는 1개
			.maxSessionsPreventsLogin(false)   // 중복 로그인 시 기존 사용자 세션 종료. true인 경우 새로운 로그인 시도가 막힘.
			.sessionRegistry(sessionRegistry())
			.expiredUrl(Constants.DEFAULT_LOGIN_URL)   // 중복 로그인시 기존 세션은 파기시키고 페이지 이동
		
		.and().and()
			.logout()   // 로그아웃 설정
			// .logoutSuccessUrl("/afterLogout")   // 세션 만료시 이동할 페이지 지정, invalidSessionUrl 메서드가 먼저 실행되고 로그인페이지로 이동하므로 이 메서드는 실행 되지 않음.
			.invalidateHttpSession(true).deleteCookies("JSESSIONID").clearAuthentication(true)   // 로그아웃 시 세션제거, 쿠키 제거

		.and()
			.exceptionHandling().accessDeniedPage("/error/403")   // 권한 없는 페이지 접근시 페이지 설정.
			.authenticationEntryPoint(ajaxAuthEntryPoint)   // 세션 만료된 상태에서 ajax로 요청시 상태 코드 403 리턴

		.and()
			.headers().frameOptions().sameOrigin();   // iFrame을 사용할 때 동일 도메인에서만 X-Frame-Options Deny 해제.

		
		// AuthenticationPrincipal 객체가 가지는 User를 상속한 Member 객체의 패스워드를 null로 변경하는게 기본 옵션이다. 기본 옵션을 꺼서 null로 초기화 하지 않도록 한다.
		// LoginUser 객체의 Member 객체에서 getPassword() 하면 인코딩된 패스워드를 리턴한다. 패스워드 변경시 기존패스워드 일치 여부를 query를 날리지 않고 바로 확인할 수 있다.
		http.getSharedObject(AuthenticationManagerBuilder.class).eraseCredentials(false);

		return http.build();
	}



	// session 관련 추가. 아래 2개의 bean이 없으면 세션 중복 에러 해결이 안 된다.
	// 세션 중복 에러: A 유저가 로그인 후 로그아웃 -> 다시 로그인할 때 'Maximum sessions of 1 for this principal exceeded' 에러가 난다.
	@Bean
	SessionRegistry sessionRegistry() {
		log.info("Bean 'SessionRegistryImpl' has been created.");
		return new SessionRegistryImpl();
	} // Register HttpSessionEventPublisher

	@Bean
	static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		log.info("Bean 'ServletListenerRegistrationBean' has been created.");
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
