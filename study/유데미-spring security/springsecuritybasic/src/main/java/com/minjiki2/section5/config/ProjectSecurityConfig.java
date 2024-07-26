package com.minjiki2.section5.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class ProjectSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // 일반적으로, 운영상에서는 아래 권한요청설정을 사용하지 않는다.
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); */ // -- 모든 요청 허가
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());*/ // -- 모든 요청 거부

        // url에 따른 인증 허가 커스터마이징
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        .requestMatchers("/notices", "/contact", "/register").permitAll());

        http.formLogin(Customizer.withDefaults()); // 스프링시큐리티가 제공하는 기본 로그인폼을 사용하도록 설정 [전통적인 폼 기반 인증]
        http.httpBasic(Customizer.withDefaults()); // HTTP 기본 인증 설정

        http.csrf(csrf -> csrf.disable()); // csrf 보안 해제
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        // BCryptPasswordEncoder로 변환!!
    }



}
