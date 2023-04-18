package com.budgetapi.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record AccountRequestDTO(
        @NotBlank
        @NotNull
        @Length(min = 5, max = 50)
        String name,
        @NotBlank
        @NotNull
        @Length(min = 3, max = 3)
        String currency
) {
}
