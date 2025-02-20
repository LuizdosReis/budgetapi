package com.budgetapi.transaction.controller;

import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.services.CreateTransaction;
import com.budgetapi.transaction.services.DeleteTransaction;
import com.budgetapi.transaction.services.UpdateTransaction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final UpdateTransaction updateTransaction;
    private final DeleteTransaction deleteTransaction;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    void create(@RequestBody @Valid TransactionRequestDTO dto) {
        this.createTransaction.execute(dto);
    }

    @PutMapping("/{id}")
    void update(@PathVariable TransactionId id, @RequestBody @Valid TransactionRequestDTO dto) {
        this.updateTransaction.execute(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void delete(@PathVariable TransactionId id) {
        this.deleteTransaction.execute(id);
    }
}
