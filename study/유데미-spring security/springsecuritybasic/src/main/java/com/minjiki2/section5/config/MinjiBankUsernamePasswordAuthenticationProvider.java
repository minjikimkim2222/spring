package com.minjiki2.section5.config;


import com.minjiki2.section5.model.Customer;
import com.minjiki2.section5.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinjiBankUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // 커스텀 인증로직
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 0. Authentication으로부터 유저 정보 불러오기
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        // 1. DB로부터 유저 정보 불러오기
        List<Customer> customers = customerRepository.findByEmail(username);

        if (customers.size() > 0){
            // 2. 불러온 유저정보를 바탕으로, 커스터마이징한 passwordEncoder.matches로 비밀번호 비교
            if (passwordEncoder.matches(pwd, customers.get(0).getPwd())){
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(customers.get(0).getRole()));

                // 해당 AuthenticationToken 생성자에게 넘긴 정보들을 채워주고, 디폴트로 인증성공여부를 true로 설정해준다!
                return new UsernamePasswordAuthenticationToken(username, pwd, authorities);

            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        } else {
            throw new BadCredentialsException("No user registered with this details!");
        }

    }

    // 유저이름, 비밀번호 인증방식 사용하고파!
    // 기존AbstractUserDetailsAuthenticationProvider의 supports 구현부 복붙
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
