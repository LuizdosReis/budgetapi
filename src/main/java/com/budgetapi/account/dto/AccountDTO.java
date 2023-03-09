package com.budgetapi.account.dto;

import java.util.UUID;

public record AccountDTO(
        UUID id,
        String name,
        String currency
){}
