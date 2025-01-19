package com.budgetapi.category.repository;

import com.budgetapi.category.model.Category;
import com.budgetapi.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CategoryRepository extends CrudRepository<Category, UUID> {
    Optional<Category> findByIdAndUser(UUID id, User user);

    Set<Category> findAllByUser(User user);

    Set<Category> findAllByUserAndDeletedIsFalse(User user);
}
