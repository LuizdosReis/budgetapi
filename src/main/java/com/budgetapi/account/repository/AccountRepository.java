package com.budgetapi.account.repository;

import com.budgetapi.account.model.Account;
import com.budgetapi.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
    Iterable<Account> findAllByUser(User user);

    Optional<Account> findByIdAndUser(UUID uuid, User user);
}
