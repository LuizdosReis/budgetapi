package com.budgetapi.category.controller;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestBody @Valid CategoryRequestDTO categoryDTO) {
        categoryService.save(categoryDTO);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable UUID id, @RequestBody @Valid CategoryRequestDTO categoryDTO) {
        categoryService.update(id, categoryDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
