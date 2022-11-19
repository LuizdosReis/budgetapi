package com.budgetapi.account.controller;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping(AccountController.BASE_URL)
public class AccountController {

    public static final String BASE_URL = "/accounts";

    private AccountRepository repository;
    private AccountMapper mapper;

    @GetMapping
    public Set<AccountDTO> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::accountToAccountDTO)
                .collect(Collectors.toSet());
    }
}
