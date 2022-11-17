package com.budgetapi.account.controller;

import com.budgetapi.account.dto.AccountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(AccountController.BASE_URL)
public class AccountController {

    public static final String BASE_URL = "/accounts";

    @GetMapping
    public Set<AccountDTO> findAll() {
        AccountDTO account = new AccountDTO("Nubank", "BRL");

        Set<AccountDTO> set = new HashSet<>();
        set.add(account);

        return set;
    }
}
