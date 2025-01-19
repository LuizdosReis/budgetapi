package com.budgetapi.category.dto;

import com.budgetapi.category.model.Type;

import java.util.UUID;

public record CategoryDTO(UUID id,
                          String name,
                          Type type) {
}