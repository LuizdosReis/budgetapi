package com.budgetapi.tag.repository;

import com.budgetapi.EnableTestcontainers;
import com.budgetapi.auditing.AuditingConfig;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.user.model.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditingConfig.class)
@EnableTestcontainers
class TagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    private User user;
    private Tag tag;
    private Tag tag2;
    private Tag tag3;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        entityManager.persist(user);

        tag = TagFactory.create(user, c -> c.name("tag_name1"));
        tag2 = TagFactory.create(user, c -> c.name("tag_name2"));
        tag3 = TagFactory.createDelete(user, c -> c.name("tag_name3"));
        tagRepository.saveAll(Set.of(tag, tag2, tag3));
    }

    @Test
    @DisplayName("save should set created date and not set updated date")
    void save_shouldSetCreatedDateAndNotSetUpdatedDate() {
        Tag createTag = TagFactory.create(user);
        tagRepository.save(createTag);
        entityManager.flush();
        entityManager.detach(createTag);

        Tag loadTag = entityManager.find(Tag.class, createTag.getId());

        assertThat(loadTag.getCreateDate()).isNotNull();
        assertThat(loadTag.getModifiedDate()).isNull();
    }

    @Test
    @DisplayName("update should not change created date and set updated date")
    void update_shouldNotChangeCreatedDateAndSetUpdatedDate() {
        Tag createTag = TagFactory.create(user);
        tagRepository.save(createTag);

        LocalDateTime createDate = createTag.getCreateDate();

        createTag.setName("updatedName");

        tagRepository.save(createTag);
        entityManager.flush();
        entityManager.detach(createTag);

        Tag loadTag = entityManager.find(Tag.class, createTag.getId());

        assertThat(loadTag.getCreateDate()).isCloseTo(createDate, byLessThan(1, ChronoUnit.MICROS));
        assertThat(loadTag.getModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("save should not allow duplicate tag names")
    void save_shouldNotAllowDuplicateTagNames() {
        Tag createTag = TagFactory.create(user);
        tagRepository.save(createTag);

        Tag duplicateTag = TagFactory.create(user);

        tagRepository.save(duplicateTag);

        assertThrows(ConstraintViolationException.class, () -> entityManager.flush());
    }


    @Test
    @DisplayName("findByIdAndUser should return tag when exists")
    void findByIdAndUser_shouldReturnTagWhenExists() {
        Optional<Tag> tagOptional = tagRepository.findByIdAndUser(tag.getId(), user);

        assertThat(tagOptional).isPresent().contains(tag);
    }

    @Test
    @DisplayName("findByIdAndUser should return empty when id does not exists")
    void findByIdAndUser_shouldReturnEmptyWhenIdDoesNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Tag> tagOptional = tagRepository.findByIdAndUser(nonExistentId, user);

        assertThat(tagOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUser should return empty when tag belongs to other user")
    void findByIdAndUser_shouldReturnEmptyWhenTagBelongsToOtherUser() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Tag otherUserTag = TagFactory.create(otherUser);
        tagRepository.save(otherUserTag);
        entityManager.flush();

        Optional<Tag> tagOptional = tagRepository.findByIdAndUser(otherUserTag.getId(), user);
        assertThat(tagOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUser should return empty when other user tries to access tag")
    void findByIdAndUser_shouldReturnEmptyWhenOtherUserTriesToAccessTag() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);

        Optional<Tag> tagOptional = tagRepository.findByIdAndUser(tag.getId(), otherUser);
        assertThat(tagOptional).isEmpty();
    }

    @Test
    @DisplayName("delete should set deleted to true")
    void delete_shouldSetDeletedToTrue() {
        tagRepository.delete(tag);
        entityManager.flush();

        Tag foundTag = tagRepository.findById(tag.getId()).orElseThrow();
        assertThat(foundTag.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("findAllByUser should return all tags")
    void findAllByUser_shouldReturnAllTags() {
        Iterable<Tag> tags = tagRepository.findAllByUser(user);
        assertThat(tags)
                .hasSize(3)
                .containsExactlyInAnyOrder(tag, tag2, tag3);
    }

    @Test
    @DisplayName("findAllByUserAndDeletedIsFalse should not return deleted tags")
    void findAllByUserAndDeletedIsFalse_shouldReturnAllNotDeletedTagsForUser() {
        Iterable<Tag> tags = tagRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(tags)
                .hasSize(2)
                .containsExactlyInAnyOrder(tag, tag2);
    }

    @Test
    @DisplayName("findAllByUser should not return tags from other user")
    void findAllByUser_shouldNotReturnTagsFromOtherUser() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Tag otherUserTag = TagFactory.create(otherUser);
        entityManager.persist(otherUserTag);
        entityManager.flush();

        Iterable<Tag> tags = tagRepository.findAllByUser(user);
        assertThat(tags)
                .hasSize(3)
                .containsExactlyInAnyOrder(tag, tag2, tag3);
    }

    @Test
    @DisplayName("findAllByUserAndDeletedIsFalse should not return tags from other user")
    void findAllByUserAndDeletedIsFalse_shouldNotReturnTagsFromOtherUser() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Tag otherUserTag = TagFactory.create(otherUser);
        entityManager.persistAndFlush(otherUserTag);

        Iterable<Tag> tags = tagRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(tags)
                .hasSize(2)
                .containsExactlyInAnyOrder(tag, tag2);
    }

    @Test
    @DisplayName("findAllByUserAndDeletedIsFalse should return tags")
    void findAllByIdInAndUser_shouldReturnTags() {
        Set<UUID> ids = Set.of(tag.getId(), tag2.getId());

        Iterable<Tag> tags = tagRepository.findAllByIdInAndUser(ids, user);
        assertThat(tags)
                .hasSize(2)
                .containsExactlyInAnyOrder(tag, tag2);
    }

    @Test
    @DisplayName("findAllByIdInAndUser should not return tags from other user")
    void findAllByIdInAndUser_shouldNotReturnTagsFromOtherUser() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Tag otherUserTag = TagFactory.create(otherUser);
        entityManager.persistAndFlush(otherUserTag);
        Set<UUID> ids = Set.of(tag.getId(), tag2.getId(), otherUserTag.getId());

        Iterable<Tag> tags = tagRepository.findAllByIdInAndUser(ids, user);
        assertThat(tags)
                .hasSize(2)
                .containsExactlyInAnyOrder(tag, tag2);
    }
}
