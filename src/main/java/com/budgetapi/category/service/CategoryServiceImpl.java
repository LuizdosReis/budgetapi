package com.budgetapi.category.service;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.mapper.CategoryMapper;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    public static final String CATEGORY_NOT_FOUND = "Category with id %s not found";

    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final UserService userService;

    @Override
    @Transactional
    public void save(CategoryRequestDTO dto) {
        Category category = mapper.toModel(dto, userService.getCurrentUser());
        repository.save(category);
    }

    @Override
    public CategoryDTO findById(UUID id) {
        return repository.findByIdAndUser(id, userService.getCurrentUser())
                .map(mapper::toDTO)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Category category = repository.findByIdAndUser(id, userService.getCurrentUser())
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
        repository.delete(category);
    }

    @Override
    public Set<CategoryDTO> findAll(boolean includeDeleted) {
        Set<Category> categories = includeDeleted ?
                repository.findAllByUser(userService.getCurrentUser()) :
                repository.findAllByUserAndDeletedIsFalse(userService.getCurrentUser());

        return mapper.toDTO(categories);
    }

    @Override
    public void update(UUID id, CategoryRequestDTO dto) {
        Category category = repository.findByIdAndUser(id, userService.getCurrentUser())
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
        mapper.updateModel(dto, category);
        repository.save(category);
    }
}
