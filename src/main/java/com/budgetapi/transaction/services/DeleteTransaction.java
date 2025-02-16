package com.budgetapi.transaction.services;

import java.util.UUID;

public interface DeleteTransaction {
    void execute(UUID transactionId);
}
