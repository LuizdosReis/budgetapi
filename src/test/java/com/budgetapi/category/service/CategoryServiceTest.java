package com.budgetapi.category.service;

import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.mapper.CategoryMapper;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.model.Type;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.budgetapi.category.service.CategoryServiceImpl.CATEGORY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = UserFactory.createUser();
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void save_shouldCallMapperAndRepositorySave() {
        CategoryRequestDTO dto = new CategoryRequestDTO("category", Type.EXPENSE);
        Category category = new Category();
        when(mapper.toModel(dto, user)).thenReturn(category);

        service.save(dto);

        verify(mapper, times(1)).toModel(dto, user);
        verify(repository, times(1)).save(category);
    }

    @Test
    void findById_shouldCallRepositoryFindByIdAndUser() {
        UUID uuid = UUID.randomUUID();
        Category category = CategoryFactory.create(user, c -> c.id(uuid));
        CategoryDTO dto = new CategoryDTO(uuid, "category", Type.EXPENSE);
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(category));
        when(mapper.toDTO(category)).thenReturn(dto);

        CategoryDTO foundCategoryDTO = service.findById(uuid);

        verify(mapper, times(1)).toDTO(category);
        verify(repository, times(1)).findByIdAndUser(uuid, user);
        assertThat(foundCategoryDTO).isEqualTo(dto);
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenCategoryNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById(uuid));

        assertThat(exception.getMessage()).contains(String.format(CATEGORY_NOT_FOUND, uuid));
    }

    @Test
    void delete_shouldCallRepositoryFindByIdAndUserAndDelete() {
        UUID uuid = UUID.randomUUID();
        Category category = CategoryFactory.create(user, c -> c.id(uuid));
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(category));

        service.delete(uuid);

        verify(repository, times(1)).findByIdAndUser(uuid, user);
        verify(repository, times(1)).delete(category);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenCategoryNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.delete(uuid));

        assertThat(exception.getMessage()).contains(String.format(CATEGORY_NOT_FOUND, uuid));
    }

    @Test
    void findAll_shouldCallFindAllByUser_whenIncludeDeletedIsFalse() {
        service.findAll(false);
        verify(repository, times(1)).findAllByUser(user);
    }

    @Test
    void findAll_shouldCallFindAllByUserAndDeletedIsFalse_whenIncludeDeletedIsTrue() {
        service.findAll(true);
        verify(repository, times(1)).findAllByUserAndDeletedIsFalse(user);
    }

    @Test
    void findAll_shouldCallMapperWithCategoriesList() {
        UUID uuid = UUID.randomUUID();
        Set<Category> categories = Set.of(CategoryFactory.create(user, c -> c.id(uuid)));
        Set<CategoryDTO> categoryDTOS = Set.of(new CategoryDTO(uuid, "category", Type.EXPENSE));
        when(repository.findAllByUser(user)).thenReturn(categories);
        when(mapper.toDTO(categories)).thenReturn(categoryDTOS);

        Set<CategoryDTO> foundCategories = service.findAll(false);

        verify(mapper, times(1)).toDTO(categories);
        verify(repository, times(1)).findAllByUser(user);
        assertThat(foundCategories).isEqualTo(categoryDTOS);
    }

    @Test
    void update_shouldCallRepositoryFindByIdAndUserAndSave() {
        UUID uuid = UUID.randomUUID();
        Category category = CategoryFactory.create(user, c -> c.id(uuid));
        CategoryRequestDTO dto = new CategoryRequestDTO("category", Type.EXPENSE);
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(category));

        service.update(uuid, dto);

        verify(repository, times(1)).findByIdAndUser(uuid, user);
        verify(mapper, times(1)).updateModel(dto, category);
        verify(repository, times(1)).save(category);
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenCategoryNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());

        CategoryRequestDTO dto = new CategoryRequestDTO("category", Type.EXPENSE);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.update(uuid, dto));

        assertThat(exception.getMessage()).contains(String.format(CATEGORY_NOT_FOUND, uuid));
    }
}
