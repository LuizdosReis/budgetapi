package com.budgetapi.account.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record AccountRequestDTO(
        @NotBlank
        @NotNull
        @Length(min = 5, max = 50)
        String name,
        @NotBlank
        @NotNull
        @Length(min = 3, max = 3)
        String currency
){}
