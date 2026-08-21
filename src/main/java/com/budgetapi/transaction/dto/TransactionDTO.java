package com.budgetapi.transaction.dto;

import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TransactionDTO(String description,
                             AccountDTO account,
                             CategoryDTO category,
                             Set<TagDTO> tags,
                             BigDecimal amount,
                             UUID id,
                             LocalDate date,
                             TransactionStatus status,
                             boolean deleted,
                             Direction direction) {
}
