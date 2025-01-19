package com.budgetapi.category.dto;

import com.budgetapi.category.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CategoryRequestDTO(
        @NotBlank
        @NotNull
        @Length(min = 5, max = 50)
        String name,
        @NotNull
        Type type
) {
}
