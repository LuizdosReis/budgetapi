package com.budgetapi.transaction.dto;

import com.budgetapi.transaction.model.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TransactionRequestDTO(@NotNull(message = "Description cannot be null")
                                    @Length(min = 5, max = 50, message = "Description must be between 5 and 50 characters")
                                    String description,
                                    @NotNull(message = "AccountId cannot be null")
                                    UUID accountId,
                                    @NotNull(message = "CategoryId cannot be null")
                                    UUID categoryId,
                                    @NotNull(message = "TagIds cannot be null")
                                    Set<UUID> tagIds,
                                    @NotNull(message = "Amount cannot be null")
                                    BigDecimal amount,
                                    @NotNull(message = "Date cannot be null")
                                    LocalDate date,
                                    @NotNull(message = "Status cannot be null")
                                    TransactionStatus status
) {
}
