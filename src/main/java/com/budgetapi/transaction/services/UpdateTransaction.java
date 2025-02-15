package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionRequestDTO;

import java.util.UUID;

public interface UpdateTransaction {
    void execute(UUID transactionId, TransactionRequestDTO dto);
}
