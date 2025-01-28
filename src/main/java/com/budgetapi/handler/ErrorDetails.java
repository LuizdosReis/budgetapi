package com.budgetapi.handler;

import java.time.LocalDateTime;

public record ErrorDetails(
        LocalDateTime timestamp,
        String message) {
}
