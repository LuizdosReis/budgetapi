package com.budgetapi.category.service;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;

import java.util.Set;
import java.util.UUID;

public interface CategoryService {
    void save(CategoryRequestDTO dto);

    CategoryDTO findById(UUID id);

    void delete(UUID id);

    Set<CategoryDTO> findAll(boolean includeDeleted);

    void update(UUID id, CategoryRequestDTO dto);
}
