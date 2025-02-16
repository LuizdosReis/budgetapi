package com.budgetapi.category.repository;

import com.budgetapi.EnableTestcontainers;
import com.budgetapi.auditing.AuditingConfig;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditingConfig.class)
@EnableTestcontainers
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        entityManager.persist(user);

        category = CategoryFactory.create(user);
        category2 = CategoryFactory.create(user);
        category3 = CategoryFactory.createDelete(user);
        entityManager.persist(category);
        entityManager.persist(category2);
        entityManager.persist(category3);
    }

    @Test
    void save_shouldSetCreatedDateAndNotSetUpdatedDate() {
        Category createdCategory = CategoryFactory.create(user);
        categoryRepository.save(createdCategory);
        entityManager.flush();
        entityManager.detach(createdCategory);

        Category loadCategory = entityManager.find(Category.class, createdCategory.getId());

        assertThat(loadCategory.getCreateDate()).isNotNull();
        assertThat(loadCategory.getModifiedDate()).isNull();
    }

    @Test
    void update_shouldNotChangeCreatedDateAndSetUpdatedDate() {
        Category createdCategory = CategoryFactory.create(user);
        categoryRepository.save(createdCategory);

        LocalDateTime createDate = createdCategory.getCreateDate();

        createdCategory.setName("updatedName");

        categoryRepository.save(createdCategory);
        entityManager.flush();
        entityManager.detach(createdCategory);

        Category loadCategory = entityManager.find(Category.class, createdCategory.getId());

        assertThat(loadCategory.getCreateDate()).isCloseTo(createDate, byLessThan(1, ChronoUnit.MICROS));
        assertThat(loadCategory.getModifiedDate()).isNotNull();
    }

    @Test
    void findByIdAndUser_shouldReturnCategoryWhenExists() {
        Optional<Category> categoryOptional = categoryRepository.findByIdAndUser(category.getId(), user);

        assertThat(categoryOptional).isPresent().contains(category);
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenIdDoesNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Category> categoryOptional = categoryRepository.findByIdAndUser(nonExistentId, user);

        assertThat(categoryOptional).isEmpty();
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenCategoryBelongsToOtherUser() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Category otherUserCategory = CategoryFactory.create(otherUser);
        entityManager.persist(otherUserCategory);
        entityManager.flush();

        Optional<Category> categoryOptional = categoryRepository.findByIdAndUser(otherUserCategory.getId(), user);
        assertThat(categoryOptional).isEmpty();
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenOtherUserTriesToAccessCategory() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);

        Optional<Category> categoryOptional = categoryRepository.findByIdAndUser(category.getId(), otherUser);
        assertThat(categoryOptional).isEmpty();
    }

    @Test
    void delete_shouldSetDeletedToTrue() {
        categoryRepository.deleteById(category.getId());
        entityManager.flush();

        Category foundCategory = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(foundCategory.isDeleted()).isTrue();
    }

    @Test
    void findAllByUser_shouldReturnAllCategoriesForUser() {
        Iterable<Category> categories = categoryRepository.findAllByUser(user);
        assertThat(categories)
                .hasSize(3)
                .containsExactlyInAnyOrder(category, category2, category3);
    }

    @Test
    void findAllByUserAndDeletedIsFalse_shouldReturnAllNotDeletedCategoriesForUser() {
        Iterable<Category> categories = categoryRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(categories)
                .hasSize(2)
                .containsExactlyInAnyOrder(category, category2);
    }

    @Test
    void findAllByUser_shouldNotReturnCategoriesFromOtherUsers() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Category otherUserCategory = CategoryFactory.create(otherUser);
        entityManager.persist(otherUserCategory);
        entityManager.flush();

        Iterable<Category> categories = categoryRepository.findAllByUser(user);
        assertThat(categories)
                .hasSize(3)
                .containsExactlyInAnyOrder(category, category2, category3);
    }

    @Test
    void findAllByUserAndDeletedIsFalse_shouldNotReturnCategoriesFromOtherUsers() {
        User otherUser = UserFactory.createUser(builder -> builder.username("otherUser"));
        entityManager.persist(otherUser);
        Category otherUserCategory = CategoryFactory.create(otherUser);
        entityManager.persist(otherUserCategory);
        entityManager.flush();

        Iterable<Category> categories = categoryRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(categories)
                .hasSize(2)
                .containsExactlyInAnyOrder(category, category2);
    }
}
