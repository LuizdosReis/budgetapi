package com.budgetapi.user.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
