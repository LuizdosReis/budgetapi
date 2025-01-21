package com.budgetapi.category.controller;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {

    public static final String BASE_URL = "/categories";

    private final CategoryService categoryService;

    @GetMapping()
    public Set<CategoryDTO> getAll(@RequestParam(required = false, defaultValue = "false") boolean includeDeleted) {
        return categoryService.findAll(includeDeleted);
    }
}
