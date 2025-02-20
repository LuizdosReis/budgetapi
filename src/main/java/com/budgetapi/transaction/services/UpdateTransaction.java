package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.TransactionId;

public interface UpdateTransaction {
    void execute(TransactionId transactionId, TransactionRequestDTO dto);
}
