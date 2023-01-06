package com.project.simplegw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableCaching
@EnableAsync
@EnableScheduling
public class SimplegwApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplegwApplication.class, args);
	}

}
