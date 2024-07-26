package com.minjiki2.section6.config;

import com.minjiki2.section6.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ProjectSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // 3. 분리된 UI를 사용하기에 -- 스프링시큐리티에게, 첫로그인이 완료되면, 항상 JSESSIONID 생성해달라고 함
        // 이 두줄없이는, 매번 보안된 API에 접근할 때마다, Augular 앱에서 자격증명을 인증해야 함
        http
                // -- 스프링프레임워크에게, 인증된 정보를 쓰레드로컬변수인 SecurityContextHolder에 저장하도록 함
            .securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
            .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        // 1. cors 허용 출처 커스터마이징
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                return config;
            }
        }));

        // 2. CSRF 관련 수정사항
            //  http.csrf(csrf -> csrf.disable()); // csrf 보안 해제
        // CsrfTokenRequestHandler -- 요청 url에, CSRF 토큰값을 생성하고 주기위해 필요함
        CsrfTokenRequestHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        http.csrf(csrf -> csrf
                // 설정한 csrfTokenRequestHandler 세팅
                .csrfTokenRequestHandler(requestHandler)
                // public api에 csrf 보호 무시
                .ignoringRequestMatchers("/contact", "/register")
                // csrf 토큰을 쿠키로 유지하게끔 한다
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // 백엔드에서, 첫로그인후, UI어플리케이션에게, 헤더와 쿠키값을 보내야 함 (UI앱도 CSRF 토큰값 알아야 하자나)
                // UI 어플리케이션에 보낼 모든 응답에, 필터 적용
            )
                // 직접 설정한 필터 add
                // BasicAuthentcationFilter[이 필터이후에만 로그인인증가능] 이후에, CsrfCookieFilter를 적용해달라는 뜻!
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);


        // url에 따른 인증 허가 커스터마이징 -- /users 인증 추가
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
                .requestMatchers("/notices", "/contact", "/register").permitAll());

        http.formLogin(Customizer.withDefaults()); // 스프링시큐리티가 제공하는 기본 로그인폼을 사용하도록 설정 [전통적인 폼 기반 인증]
        http.httpBasic(Customizer.withDefaults()); // HTTP 기본 인증 설정

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        // BCryptPasswordEncoder로 변환!!
    }



}
