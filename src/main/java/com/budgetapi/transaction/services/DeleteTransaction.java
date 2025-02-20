package com.budgetapi.transaction.services;

import com.budgetapi.transaction.model.TransactionId;

public interface DeleteTransaction {
    void execute(TransactionId transactionId);
}
