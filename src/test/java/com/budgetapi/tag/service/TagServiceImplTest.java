package com.budgetapi.tag.service;

import com.budgetapi.erro.NotFoundException;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.mapper.TagMapper;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.budgetapi.tag.service.TagServiceImpl.TAG_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl service;

    @Mock
    private TagMapper mapper;

    @Mock
    private UserService userService;

    @Mock
    private TagRepository repository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = UserFactory.createUser();
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    @DisplayName("save should call mapper and repository")
    void save_shouldCallMapperAndRepository() {
        TagRequestDTO dto = new TagRequestDTO("tag");
        Tag tag = new Tag();
        when(mapper.toModel(dto, user)).thenReturn(tag);

        service.save(dto);

        verify(mapper, times(1)).toModel(dto, user);
        verify(repository, times(1)).save(tag);
    }

    @Test
    @DisplayName("update should call repository findByIdAndUser mapper updateModel and save")
    void update_shouldCallRepositoryFindByIdAndUserMapperUpdateModelAndSave() {
        UUID uuid = UUID.randomUUID();
        Tag tag = TagFactory.create(user, t -> t.id(uuid));
        TagRequestDTO dto = new TagRequestDTO("tag");
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(tag));

        service.update(uuid, dto);

        verify(repository, times(1)).findByIdAndUser(uuid, user);
        verify(mapper, times(1)).updateModel(dto, tag);
        verify(repository, times(1)).save(tag);
    }

    @Test
    @DisplayName("update should throw NotFoundException when tag not exists")
    void update_shouldThrowNotFoundExceptionWhenTagNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());
        TagRequestDTO dto = new TagRequestDTO("tag");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.update(uuid, dto));

        assertThat(exception.getMessage()).contains(String.format(TAG_NOT_FOUND, uuid));
    }

    @Test
    @DisplayName("findById should call repository findByIdAndUser and mapper toDTO")
    void findById_shouldCallRepositoryFindByIdAndUser() {
        UUID uuid = UUID.randomUUID();
        Tag tag = TagFactory.create(user, t -> t.id(uuid));
        TagDTO dto = new TagDTO(uuid, "tag");
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(tag));
        when(mapper.toDTO(tag)).thenReturn(dto);

        TagDTO foundTagDTO = service.findById(uuid);

        verify(mapper, times(1)).toDTO(tag);
        verify(repository, times(1)).findByIdAndUser(uuid, user);
        assertThat(foundTagDTO).isEqualTo(dto);
    }

    @Test
    @DisplayName("findById should throw NotFoundException when tag not exists")
    void findById_shouldThrowNotFoundExceptionWhenTagNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById(uuid));

        assertThat(exception.getMessage()).contains(String.format(TAG_NOT_FOUND, uuid));
    }

    @Test
    @DisplayName("delete should call repository findByIdAndUser and delete")
    void delete_shouldCallRepositoryFindByIdAndUserAndDelete() {
        UUID uuid = UUID.randomUUID();
        Tag tag = TagFactory.create(user, t -> t.id(uuid));
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.of(tag));

        service.delete(uuid);

        verify(repository, times(1)).findByIdAndUser(uuid, user);
        verify(repository, times(1)).delete(tag);
    }

    @Test
    @DisplayName("delete should throw NotFoundException when tag not exists")
    void delete_shouldThrowNotFoundExceptionWhenTagNotExists() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByIdAndUser(uuid, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.delete(uuid));

        assertThat(exception.getMessage()).contains(String.format(TAG_NOT_FOUND, uuid));
    }

    @Test
    @DisplayName("findAll should call findAllByUser when includeDeleted is true")
    void findAll_shouldCallFindAllByUser_whenIncludeDeletedIsFalse() {
        service.findAll(true);
        verify(repository, times(1)).findAllByUser(user);
    }

    @Test
    @DisplayName("findAll should call findAllByUserAndDeletedIsFalse when includeDeleted is false")
    void findAll_shouldCallFindAllByUserAndDeletedIsFalse_whenIncludeDeletedIsFalse() {
        service.findAll(false);
        verify(repository, times(1)).findAllByUserAndDeletedIsFalse(user);
    }

    @Test
    @DisplayName("findAll should call mapper with categories list")
    void findAll_shouldCallMapperWithCategoriesList() {
        UUID uuid = UUID.randomUUID();
        Set<Tag> tags = Set.of(TagFactory.create(user, c -> c.id(uuid)));
        Set<TagDTO> dtos = Set.of(new TagDTO(uuid, "tag_name"));
        when(repository.findAllByUser(user)).thenReturn(tags);
        when(mapper.toDTO(tags)).thenReturn(dtos);

        Set<TagDTO> foundTags = service.findAll(true);

        verify(repository, times(1)).findAllByUser(user);
        verify(mapper, times(1)).toDTO(tags);
        assertThat(foundTags).isEqualTo(dtos);
    }
}
