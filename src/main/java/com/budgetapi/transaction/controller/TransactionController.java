package com.budgetapi.transaction.controller;

import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.services.CreateTransaction;
import com.budgetapi.transaction.services.UpdateTransaction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(TransactionController.BASE_URL)
public class TransactionController {
    public static final String BASE_URL = "/transactions";

    private final CreateTransaction createTransaction;
    private final UpdateTransaction updateTransaction;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    void create(@RequestBody @Valid TransactionRequestDTO dto) {
        this.createTransaction.execute(dto);
    }

    @PutMapping("/{id}")
    void update(@PathVariable UUID id, @RequestBody @Valid TransactionRequestDTO dto) {
        this.updateTransaction.execute(id, dto);
    }
}
