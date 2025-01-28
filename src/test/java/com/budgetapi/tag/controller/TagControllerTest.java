package com.budgetapi.tag.controller;

import com.budgetapi.AbstractControllerTest;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.budgetapi.tag.service.TagServiceImpl.TAG_NOT_FOUND;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TagService tagService;

    @Test
    @DisplayName("GET /tags returns 200 OK with category list with name and id")
    void getAll_returnsAllCategories() throws Exception {
        Set<TagDTO> tags = Set.of(
                new TagDTO(UUID.randomUUID(), "tag_name1"),
                new TagDTO(UUID.randomUUID(), "tag_name2"),
                new TagDTO(UUID.randomUUID(), "tag_name3")
        );

        when(tagService.findAll(anyBoolean())).thenReturn(tags);

        this.mockMvc.perform(get(TagController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(tags.size())))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(tags.stream().map(TagDTO::id).map(UUID::toString).toArray())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(tags.stream().map(TagDTO::name).toArray())));
    }

    @Test
    @DisplayName("GET /tags returns 200 OK with empty list when no tags are found")
    void getAll_returnsEmptyList() throws Exception {
        when(tagService.findAll(anyBoolean())).thenReturn(Set.of());

        this.mockMvc.perform(get(TagController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /tags calls service with false by default")
    void getAll_callsServiceWithFalse_whenIncludedDeletedIsNotProvide() throws Exception {
        this.mockMvc.perform(get(TagController.BASE_URL));

        verify(tagService, times(1)).findAll(false);
    }

    @Test
    @DisplayName("GET /tags calls service with true when includeDeleted is true")
    void getAll_callsServiceWithTrue() throws Exception {
        this.mockMvc.perform(get(TagController.BASE_URL).queryParam("includeDeleted", "true"));

        verify(tagService, times(1)).findAll(true);
    }

    @Test
    @DisplayName("GET /tags calls service with false when includeDeleted is false")
    void getAll_callsServiceWithFalse() throws Exception {
        this.mockMvc.perform(get(TagController.BASE_URL).queryParam("includeDeleted", "false"));

        verify(tagService, times(1)).findAll(false);
    }

    @Test
    @DisplayName("GET /tags/{id} returns 200 OK with category with name and id")
    void getById_returnsCategory() throws Exception {
        UUID id = UUID.randomUUID();
        TagDTO tag = new TagDTO(id, "tag_name1");

        when(tagService.findById(id)).thenReturn(tag);

        this.mockMvc.perform(get(TagController.BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(tag.name()));
    }

    @Test
    @DisplayName("GET /tags/{id} returns 404 not found when category is not found")
    void getById_returns404_whenCategoryIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(tagService.findById(id)).thenThrow(new NotFoundException(String.format(TAG_NOT_FOUND, id)));

        this.mockMvc.perform(get(TagController.BASE_URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format(TAG_NOT_FOUND, id))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST /tags returns 201 and calls save when payload is valid")
    void post_createsAndReturns201_whenPayloadIsValid() throws Exception {
        TagRequestDTO payload = new TagRequestDTO("tag_name");

        this.mockMvc.perform(post(TagController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(tagService, times(1)).save(payload);
    }

    @ParameterizedTest
    @DisplayName("POST /tags return 400 bad request and do not when payload is invalid")
    @MethodSource("invalidTagRequestDTOs")
    void post_doNotSaveAndReturns400_whenPayloadIsInvalid(TagRequestDTO payload, String fieldErrorMessage) throws Exception {
        this.mockMvc.perform(post(TagController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Field Validation Errors")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors[*].message", contains(fieldErrorMessage)))
                .andExpect(jsonPath("$.fieldErrors[*].rejectedValue", contains(payload.name())));

        verifyNoInteractions(tagService);
    }

    @Test
    @DisplayName("POST /tags do not save category and returns 400 when payload is empty")
    void post_doNotSaveAndReturns400_whenPayloadIsEmpty() throws Exception {
        this.mockMvc.perform(post(TagController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tagService);
    }

    private static Stream<Arguments> invalidTagRequestDTOs() {
        return Stream.of(
                Arguments.of(new TagRequestDTO(""), "Name must be between 5 and 50 characters"),
                Arguments.of(new TagRequestDTO(null), "Name cannot be null"),
                Arguments.of(new TagRequestDTO("a"), "Name must be between 5 and 50 characters"),
                Arguments.of(new TagRequestDTO("a".repeat(51)), "Name must be between 5 and 50 characters")
        );
    }
}
