package com.minjiki2.section4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    고객 계좌 정보 컨트롤러
 */
@RestController
public class AccountController {

    @GetMapping("/myAccount")
    public String getAccountDetails(){
        return "Here are the account details from the DB";
    }
}
