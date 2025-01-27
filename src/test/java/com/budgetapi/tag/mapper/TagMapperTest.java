package com.budgetapi.tag.mapper;

import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TagMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser(builder -> builder.id(UUID.randomUUID()));
    }

    @Test
    @DisplayName("toModel should create tag with correct fields")
    void toModel_shouldCreateTagWithCorrectFields() {
        TagRequestDTO tagRequestDTO = new TagRequestDTO("tag");

        Tag tag = TagMapper.MAPPER.toModel(tagRequestDTO, user);

        assertThat(tag.getName()).isEqualTo(tagRequestDTO.name());
        assertThat(tag.getId()).isNull();
        assertThat(tag.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("updateModel should update with correct fields")
    void updateModel_shouldUpdateWithCorrectFields() {
        TagRequestDTO tagRequestDTO = new TagRequestDTO("tag");
        Tag tag = TagFactory.create(user, t -> t.id(UUID.randomUUID()));

        TagMapper.MAPPER.updateModel(tagRequestDTO, tag);

        assertThat(tag.getName()).isEqualTo(tagRequestDTO.name());
        assertThat(tag.getId()).isNotNull();
        assertThat(tag.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("toDTO should create tagDTO with correct fields")
    void toDTO_shouldCreateTagDTOWithCorrectFields() {
        Tag tag = TagFactory.create(user, t -> t.id(UUID.randomUUID()));

        TagDTO dto = TagMapper.MAPPER.toDTO(tag);

        assertThat(dto.id()).isEqualTo(tag.getId());
        assertThat(dto.name()).isEqualTo(tag.getName());
    }

}
