package com.budgetapi.transaction.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record TransactionSearchCriteria(String searchTerm,
                                        boolean nonDeleted,
                                        LocalDate sinceDate,
                                        LocalDate untilDate,
                                        Set<UUID> accountIds,
                                        Set<UUID> categoryIds,
                                        Set<UUID> tagIds) {
}
