package com.budgetapi.category.mapper;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.model.Category;
import com.budgetapi.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper MAPPER = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    Category toModel(CategoryRequestDTO dto, User user);

    CategoryDTO toDTO(Category category);

    Set<CategoryDTO> toDTO(Set<Category> categories);

    void updateModel(CategoryRequestDTO dto, @MappingTarget Category category);
}
