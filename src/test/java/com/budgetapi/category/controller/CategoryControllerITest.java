package com.budgetapi.category.controller;

import com.budgetapi.ClearDatabase;
import com.budgetapi.EnableTestcontainers;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.model.Type;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ClearDatabase
@EnableTestcontainers
public class CategoryControllerITest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFactory.createUser());
    }

    @Test
    @DisplayName("DELETE /category/{id} deletes and returns 204")
    void delete_deletesAndReturns204_whenCategoryExists() throws Exception {
        Category category = categoryRepository.save(CategoryFactory.create(user));

        this.mockMvc.perform(delete(CategoryController.BASE_URL + "/" + category.getId()))
                .andExpect(status().isNoContent());

        Optional<Category> categoryOptional = categoryRepository.findById(category.getId());
        assertThat(categoryOptional).isPresent();
        assertThat(categoryOptional.get().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("PUT /category/{id} changes category name and returns 200")
    void put_updatesCategoryAndReturns200_whenCategoryExists() throws Exception {
        Category category = categoryRepository.save(CategoryFactory.create(user));
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("new category", Type.INCOME);

        this.mockMvc.perform(put(CategoryController.BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk());

        Optional<Category> updatedCategoryOptional = categoryRepository.findById(category.getId());
        assertThat(updatedCategoryOptional).isPresent();
        Category updatedCategory = updatedCategoryOptional.get();
        assertThat(updatedCategory.getName()).isEqualTo(categoryRequestDTO.name());
        assertThat(updatedCategory.getType()).isEqualTo(categoryRequestDTO.type());
    }
}
