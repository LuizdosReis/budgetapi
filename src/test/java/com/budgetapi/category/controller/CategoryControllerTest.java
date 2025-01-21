package com.budgetapi.category.controller;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.model.Type;
import com.budgetapi.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@WithMockUser
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("GET all categories returns a list of categories")
    void getAll_returnsAllCategories() throws Exception {
        Set<CategoryDTO> categories = Set.of(
                new CategoryDTO(UUID.randomUUID(), "category1", Type.EXPENSE),
                new CategoryDTO(UUID.randomUUID(), "category2", Type.EXPENSE),
                new CategoryDTO(UUID.randomUUID(), "category3", Type.EXPENSE)
        );

        when(categoryService.findAll(anyBoolean())).thenReturn(categories);

        this.mockMvc.perform(get(CategoryController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(categories.size())))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(categories.stream().map(CategoryDTO::id).map(UUID::toString).toArray())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(categories.stream().map(CategoryDTO::name).toArray())))
                .andExpect(jsonPath("$[*].type", containsInAnyOrder(categories.stream().map(CategoryDTO::type).map(Type::toString).toArray())));
    }

    @Test
    @DisplayName("GET all categories returns an empty list when no categories are found")
    void getAll_returnsEmptyList() throws Exception {
        when(categoryService.findAll(anyBoolean())).thenReturn(Set.of());

        this.mockMvc.perform(get(CategoryController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all categories call service with false by default")
    void getAll_callsServiceWithFalse_whenIncludedDeletedIsNotProvide() throws Exception {
        this.mockMvc.perform(get(CategoryController.BASE_URL));

        verify(categoryService, times(1)).findAll(false);
    }

    @Test
    @DisplayName("GET all categories call service with true when includeDeleted is true")
    void getAll_callsServiceWithTrue() throws Exception {
        this.mockMvc.perform(get(CategoryController.BASE_URL).queryParam("includeDeleted", "true"));

        verify(categoryService, times(1)).findAll(true);
    }

    @Test
    @DisplayName("GET all categories call service with false when includeDeleted is false")
    void getAll_callsServiceWithFalse() throws Exception {
        this.mockMvc.perform(get(CategoryController.BASE_URL).queryParam("includeDeleted", "false"));

        verify(categoryService, times(1)).findAll(false);
    }
}
