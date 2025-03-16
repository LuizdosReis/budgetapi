package com.budgetapi.category.controller;

import com.budgetapi.ClearDatabase;
import com.budgetapi.EnableTestcontainers;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
}
