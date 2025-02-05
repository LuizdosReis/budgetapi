package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionRequestDTO;

public interface CreateTransaction {
    void execute(TransactionRequestDTO dto);
}
