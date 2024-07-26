package com.minjiki2.section4.controller;

import com.minjiki2.section4.repository.CustomerRepository;
import com.minjiki2.section4.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder 의존성 주입!!

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        Customer savedCustomer = null;
        ResponseEntity response = null;

        try {
            customer.setPwd(passwordEncoder.encode(customer.getPwd())); // 비밀번호를 해싱알고리즘으로 해시값으로 변환후, DB에 저장 !!
            savedCustomer = customerRepository.save(customer);

            if (savedCustomer.getId() > 0) {
                response = ResponseEntity.status(HttpStatus.CREATED).body("Given user details are successfully registerd");
            }
        } catch (Exception ex){
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occurred due to " + ex.getMessage());
        }

        return response;
    }
}
