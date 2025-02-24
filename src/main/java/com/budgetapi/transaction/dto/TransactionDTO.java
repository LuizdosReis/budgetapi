package com.budgetapi.transaction.dto;

import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record TransactionDTO(String description,
                             AccountDTO account,
                             CategoryDTO category,
                             Set<TagDTO> tags,
                             BigDecimal amount,
                             TransactionId id,
                             LocalDate date,
                             TransactionStatus status,
                             boolean deleted) {
}
