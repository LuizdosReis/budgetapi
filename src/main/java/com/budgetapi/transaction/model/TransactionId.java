package com.budgetapi.transaction.model;

import org.springframework.util.Assert;

import java.util.UUID;

public record TransactionId(UUID id) {

    public TransactionId {
        Assert.notNull(id, "id must not be null");
    }

    public TransactionId() {
        this(UUID.randomUUID());
    }
}
