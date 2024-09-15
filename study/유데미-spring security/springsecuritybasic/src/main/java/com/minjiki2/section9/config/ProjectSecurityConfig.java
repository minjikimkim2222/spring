package com.minjiki2.section9.config;

import com.minjiki2.section9.filter.CsrfCookieFilter;
import com.minjiki2.section9.filter.JWTTokenGeneratorFilter;
import com.minjiki2.section9.filter.JWTTokenValidatorFilter;
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

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ProjectSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // 0. 첫로그인시, 사용자에게 JSESSION 토큰말고, JWT 토큰 인증 사용하게끔
        http
            .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 1. cors 허용 출처 커스터마이징
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                // 프론트 UI가 JWT 토큰을 담은, Authoriztion 헤더도 읽을 수 있게끔!
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                return config;
            }
        }));

        // 2. CSRF 관련 수정사항
        // CsrfTokenRequestHandler -- 요청 url에, CSRF 토큰값을 생성하고 주기위해 필요함
        CsrfTokenRequestHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        http.csrf(csrf -> csrf
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/contact", "/register")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // 로그인 인증 이후, CsrfCookie 생성되게끔 필터
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            // 로그인 인증 이후, JWT 토큰 생성하도록
            .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
            // JWTTokenValidatorFilter가, BasicAuthenticationFilter 이전에 실행되도록
            .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class);

        // 3. url에 따른 인증 허가 커스터마이징
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount").hasRole("USER")
                .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/myLoans").hasRole("USER")
                .requestMatchers("/myCards").hasRole("USER")
                .requestMatchers("/user").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/register", "/invalidSession", "/apiLogin").permitAll());

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
