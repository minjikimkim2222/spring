package com.minjiki2.section4.config;

import com.minjiki2.section4.model.Customer;
import com.minjiki2.section4.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinjiBankUserDetails implements UserDetailsService {

    private final CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userName, password = null;
        List<GrantedAuthority> authorities = null;

        List<Customer> customers = customerRepository.findByEmail(username);// 여기서 username이 식별자인데, 여기선 email이 식별자 역할임 !!

        if (customers.size() == 0){
            throw new UsernameNotFoundException("User details not found for the user : " + username);
        } else {
            userName = customers.get(0).getEmail();
            password = customers.get(0).getPwd();
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(customers.get(0).getRole()));
        }

        return new User(userName, password, authorities);
    }
}
