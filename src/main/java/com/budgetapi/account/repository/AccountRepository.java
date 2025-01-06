package com.budgetapi.account.repository;

import com.budgetapi.account.model.Account;
import com.budgetapi.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
    List<Account> findAllByUser(User user);

    List<Account> findAllByUserAndDeletedIsFalse(User user);

    Optional<Account> findByIdAndUser(UUID uuid, User user);
}
