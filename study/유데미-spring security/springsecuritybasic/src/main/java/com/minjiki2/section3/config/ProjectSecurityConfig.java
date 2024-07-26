package com.minjiki2.section3.config;

import com.minjiki2.section3.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

//@Configuration
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

    // 1. UserDetailsManager -- InMemotyUserDetailsManager :: 추천하는 방법은 아님.
    // application.properties에 user, password 설정 대신, InMemoryUserDetailsManager를 이용한 유저 생성
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(){
//        UserDetails admin = User
//                .withUsername("admin")
//                .password("12345")
//                .authorities("admin")
//                .build();
//
//        UserDetails user = User
//                .withUsername("user")
//                .password("12345")
//                .authorities("read")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
        // 가장 간단한 PasswordEncoder로, 비밀번호에 암호화나 해싱값을 수행하지 않고, 간단히 비밀번호 값을 일반 텍스트 비밀번호로 입력 -- 그렇기에 노추천 !!
    }

    // 2. UserDetailsManager -- 두번째 구현체, JdbcUserDetailsManager
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource){
//        return new JdbcUserDetailsManager(dataSource);
//    }


}
