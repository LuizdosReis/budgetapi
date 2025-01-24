package com.budgetapi.category.controller;

import com.budgetapi.AbstractControllerTest;
import com.budgetapi.category.dto.CategoryDTO;
import com.budgetapi.category.dto.CategoryRequestDTO;
import com.budgetapi.category.model.Type;
import com.budgetapi.category.service.CategoryService;
import com.budgetapi.erro.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.budgetapi.category.service.CategoryServiceImpl.CATEGORY_NOT_FOUND;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andDo(print())
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
                .andDo(print())
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

    @Test
    @DisplayName("GET by id returns a category when category is found")
    void getById_returnsCategory() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryDTO category = new CategoryDTO(id, "category", Type.EXPENSE);

        when(categoryService.findById(id)).thenReturn(category);

        this.mockMvc.perform(get(CategoryController.BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(category.name()))
                .andExpect(jsonPath("$.type").value(category.type().toString()));
    }

    @Test
    @DisplayName("GET by id returns 404 when category is not found")
    void getById_returns404_whenCategoryIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(categoryService.findById(id)).thenThrow(new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));

        this.mockMvc.perform(get(CategoryController.BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format(CATEGORY_NOT_FOUND, id))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST category and returns 201 when payload is valid")
    void postCategory_createsAndReturns201_whenPayloadIsValid() throws Exception {
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("category", Type.EXPENSE);

        this.mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(categoryService, times(1)).save(categoryRequestDTO);
    }

    @ParameterizedTest
    @DisplayName("POST category do not save category and returns 400 when payload is invalid")
    @MethodSource("invalidCategoryRequestDTOs")
    void postCategory_doNotSaveCategoryAndReturns400_whenPayloadIsInvalid(CategoryRequestDTO payload) throws Exception {
        this.mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("POST category do not save category and returns 400 when payload is empty")
    void postCategory_doNotSaveCategoryAndReturns400_whenPayloadIsEmpty() throws Exception {
        this.mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    private static Stream<CategoryRequestDTO> invalidCategoryRequestDTOs() {
        return Stream.of(
                new CategoryRequestDTO("", Type.EXPENSE),
                new CategoryRequestDTO(null, Type.EXPENSE),
                new CategoryRequestDTO("a", Type.EXPENSE),
                new CategoryRequestDTO("a".repeat(51), Type.EXPENSE),
                new CategoryRequestDTO("category", null)
        );
    }

    @Test
    @DisplayName("PUT category updates category and returns 200 when payload is valid")
    void putCategory_updateCategoryAndReturns200_whenPayloadIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("category", Type.EXPENSE);

        this.mockMvc.perform(put(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(categoryService, times(1)).update(id, categoryRequestDTO);
    }

    @Test
    @DisplayName("PUT category returns 404 when category is not found")
    void putCategory_returns404_whenCategoryIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("category", Type.EXPENSE);

        doThrow(new NotFoundException(String.format(CATEGORY_NOT_FOUND, id))).when(categoryService).update(id, categoryRequestDTO);

        this.mockMvc.perform(put(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format(CATEGORY_NOT_FOUND, id))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

    }

    @ParameterizedTest
    @DisplayName("PUT category do not update category and returns 400 when payload is invalid")
    @MethodSource("invalidCategoryRequestDTOs")
    void updateCategory_doNotUpdateCategoryAndReturns400_whenPayloadIsInvalid(CategoryRequestDTO payload) throws Exception {
        UUID id = UUID.randomUUID();
        this.mockMvc.perform(put(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("PUT category do not update category and returns 400 when payload is empty")
    void updateCategory_doNotUpdateCategoryAndReturns400_whenPayloadIsEmpty() throws Exception {
        UUID id = UUID.randomUUID();
        this.mockMvc.perform(put(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("DELETE category returns 204")
    void deleteCategory_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        this.mockMvc.perform(delete(CategoryController.BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(id);
    }

    @Test
    @DisplayName("DELETE category returns 404 when category is not found")
    void deleteCategory_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new NotFoundException(String.format(CATEGORY_NOT_FOUND, id))).when(categoryService).delete(id);

        this.mockMvc.perform(delete(CategoryController.BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format(CATEGORY_NOT_FOUND, id))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));

        verify(categoryService, times(1)).delete(id);
    }
}
