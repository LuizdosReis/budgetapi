package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.model.TransactionId;

public interface GetTransaction {
    TransactionDTO execute(TransactionId id);
}
