package com.budgetapi.tag.repository;

import com.budgetapi.tag.model.Tag;
import com.budgetapi.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagRepository extends CrudRepository<Tag, UUID> {
    Optional<Tag> findByIdAndUser(UUID uuid, User user);

    Set<Tag> findAllByUser(User user);

    Set<Tag> findAllByUserAndDeletedIsFalse(User user);
}
