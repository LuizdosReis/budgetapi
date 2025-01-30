package com.budgetapi.transaction.repository;

import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndAccountUser(UUID id, User user);
}
