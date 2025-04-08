package com.budgetapi.tag.controller;

import com.budgetapi.ClearDatabase;
import com.budgetapi.EnableTestcontainers;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
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
class TagControllerITest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFactory.createUser());
    }

    @Test
    @DisplayName("DELETE /tags/{id} deletes and returns 204")
    void delete_deletesAndReturns204_whenCategoryExists() throws Exception {
        Tag tag = tagRepository.save(TagFactory.create(user));

        this.mockMvc.perform(delete(TagController.BASE_URL + "/" + tag.getId()))
                .andExpect(status().isNoContent());

        Optional<Tag> tagOptional = tagRepository.findById(tag.getId());
        assertThat(tagOptional).isPresent();
        assertThat(tagOptional.get().isDeleted()).isTrue();
    }
}
