package com.budgetapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRequestDTO(
        @NotBlank
        @NotNull
        @Length(min = 5, max = 50)
        String username,
        @NotBlank
        @NotNull
        @Length(min = 8)
        String password
) {
}
