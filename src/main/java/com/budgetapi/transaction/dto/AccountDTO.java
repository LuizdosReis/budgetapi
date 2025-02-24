package com.budgetapi.transaction.dto;

import java.util.UUID;

public record AccountDTO(
        UUID id,
        String name,
        String currency
) {
}
