package com.budgetapi.handler;

import java.time.LocalDateTime;
import java.util.Set;

public record ValidationErrorDetails(
        LocalDateTime timestamp,
        String message,
        Set<FieldError> fieldErrors,
        Set<GlobalError> globalErrors) {
}
