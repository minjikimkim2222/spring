package com.minjiki2.section9.filter;

import com.minjiki2.section9.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

// 각 요청당 한번만 실행되었음 좋겠어서, OncePerRequestFilter를 extends함
/*
    사용자의 인증정보를 바탕으로(Authentication),
        - JWT 토큰을 생성하고
        - 이 토큰을 응답헤더에 추가하는 것
 */
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 사용자 로그인 이후, 쓰레드로컬변수인 SecurityContextHolder에 인증과정 이후 생성된 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) { // 사용자 인증정보가 존재하는 경우에만, JWT 토큰 생성로직 추가

            Environment env = getEnvironment(); // 환경변수 접근을 위해

            if (env != null) {
                // 환경변수에서 JWT 비밀키를 가져오고, 비밀키가 존재하지 않으면 ApplicationConstants.JWT_SECRET_DEFAULT_VALUE 사용
                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                // 비밀키를 SecretKey 객체로 변환
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // JWT 토큰 생성 -- issuer :: 토큰발급자, subject :: 토큰 주제
                String jwt = Jwts.builder().issuer("Minji Bank").subject("JWT Token")
                        .claim("username", authentication.getName())
                        .claim("authorities", authentication.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact(); // 비밀키를 사용해, 토큰 서명 (signature) + compact로 최종적으로 jwt 문자열 생성

                // 응답헤더에 JWT 헤더 추가
                response.setHeader(ApplicationConstants.JWT_HEADER, jwt);
            }
        }

        filterChain.doFilter(request, response);
    }

    // 해당 JWT 생성 필터는, 오직 로그인이후에만, 필터를 실행되도록 설정 !
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/user");
    }
}
