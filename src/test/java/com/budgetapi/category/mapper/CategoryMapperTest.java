package com.budgetapi.category.mapper;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.model.Type;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
    }

    @Test
    void mapToModel_shouldCreateCategoryWithCorrectFields() {
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("category", Type.EXPENSE);

        Category category = CategoryMapper.MAPPER.toModel(categoryRequestDTO, user);

        assertThat(category.getName()).isEqualTo(categoryRequestDTO.name());
        assertThat(category.getType()).isEqualTo(categoryRequestDTO.type());
        assertThat(category.getId()).isNull();
        assertThat(category.getUser()).isEqualTo(user);
    }

    @Test
    void mapToDTO_shouldCreateCategoryDTOWithCorrectFields() {
        Category category = CategoryFactory.create(user, c -> c.id(UUID.randomUUID()));

        CategoryDTO dto = CategoryMapper.MAPPER.toDTO(category);

        assertThat(dto.id()).isEqualTo(category.getId());
        assertThat(dto.name()).isEqualTo(category.getName());
        assertThat(dto.type()).isEqualTo(category.getType());
    }

    @Test
    void mapToDTOS_shouldCreateCategoryDTOWithCorrectFields() {
        Category category = CategoryFactory.create(user, c -> c.id(UUID.randomUUID()));
        Set<Category> categories = Set.of(category);

        Set<CategoryDTO> categoryDTOS = CategoryMapper.MAPPER.toDTO(categories);

        assertThat(categoryDTOS).hasSize(1);
        CategoryDTO dto = categoryDTOS.iterator().next();
        assertThat(dto.id()).isEqualTo(category.getId());
        assertThat(dto.name()).isEqualTo(category.getName());
        assertThat(dto.type()).isEqualTo(category.getType());
    }

    @Test
    void updateModel_shouldUpdateWithCorrectFields() {
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("income", Type.INCOME);
        Category category = CategoryFactory.create(user, c -> c.id(UUID.randomUUID()));

        CategoryMapper.MAPPER.updateModel(categoryRequestDTO, category);

        assertThat(category.getName()).isEqualTo(categoryRequestDTO.name());
        assertThat(category.getType()).isEqualTo(categoryRequestDTO.type());
        assertThat(category.getId()).isNotNull();
        assertThat(category.getUser()).isEqualTo(user);
    }
}
