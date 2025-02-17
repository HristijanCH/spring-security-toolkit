package com.kiko.controller;

import com.kiko.models.Account;
import com.kiko.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountsRepository;

    @GetMapping("/myAccount")
    public Account getAccountDetails(@RequestParam long id) {
        return accountsRepository.findByCustomerId(id);
    }

}
