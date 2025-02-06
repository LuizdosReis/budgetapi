package com.budgetapi.transaction.controller;

import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.services.CreateTransaction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(TransactionController.BASE_URL)
public class TransactionController {
    public static final String BASE_URL = "/transactions";

    private final CreateTransaction createTransaction;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    void create(@RequestBody @Valid TransactionRequestDTO dto) {
        this.createTransaction.execute(dto);
    }
}
