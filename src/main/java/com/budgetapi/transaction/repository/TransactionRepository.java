package com.budgetapi.transaction.repository;

import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.user.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByIdAndAccountUser(UUID id, User user);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags ")
    Set<Transaction> findAllFetchTags();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags where t.id = :id")
    Optional<Transaction> findByIdFetchTags(UUID id);
}
