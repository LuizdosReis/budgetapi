package com.budgetapi.tag.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record TagRequestDTO(
        @NotNull(message = "Name cannot be null")
        @Length(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
        String name) {
}
