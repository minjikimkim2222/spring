package com.minjiki2.section3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity // optional -- Spring Security 기능 호라성화
public class MinjiBankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinjiBankBackendApplication.class, args);
	}

}
