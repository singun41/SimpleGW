package com.project.simplegw;


import org.springframework.boot.SpringApplication;
// import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class SimplegwApplication {
	public static void main(String[] args) {
		SpringApplication.run(SimplegwApplication.class, args); // 웹앱으로 실행하려면 아래 코드 주석하고, 이 코드 주석 해제할 것.

		// 자바앱으로 실행하기 위해 추가. test 클래스에서 테스트하면 되서 사실 필요없는데 일단 써 놓음.
		// SpringApplication app = new SpringApplication(SimplegwApplication.class);
		// app.setWebApplicationType(WebApplicationType.NONE);
		// app.run(args);
	}
}
