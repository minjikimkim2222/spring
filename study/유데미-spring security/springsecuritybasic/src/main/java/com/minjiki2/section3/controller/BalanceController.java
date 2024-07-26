package com.minjiki2.section3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    거래내역과 잔고 반환 컨트롤러
 */
@RestController
public class BalanceController {
    @GetMapping("/myBalance")
    public String getBalanceDetails(){
        return "Here are the balance details from the DB";
    }
}
