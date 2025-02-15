package com.budgetapi.transaction.repository;

import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndAccountUser(UUID id, User user);

    Page<Transaction> findByAccountUser(User user, Pageable pageable);

    Page<Transaction> findByAccountUserAndDeletedFalse(User user, Pageable pageable);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags ")
    Set<Transaction> findAllFetchTags();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags where t.id = :id")
    Optional<Transaction> findByIdFetchTags(UUID id);
}
