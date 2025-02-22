package com.budgetapi.transaction.repository;

import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.specification.TransactionSpecification;
import com.budgetapi.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, TransactionId>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByIdAndAccountUser(TransactionId id, User user);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags ")
    Set<Transaction> findAllFetchTags();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.tags where t.id = :id")
    Optional<Transaction> findByIdFetchTags(TransactionId id);

    default Page<Transaction> findAllBy(TransactionSearchCriteria criteria, User user, Pageable pageable) {
        Assert.notNull(user, "user must not be null");
        Specification<Transaction> specification = Specification.where(
                TransactionSpecification.descriptionContains(criteria.searchTerm())
                        .or(TransactionSpecification.amountContains(criteria.searchTerm()))
                        .or(TransactionSpecification.categoryNameContains(criteria.searchTerm()))
                        .or(TransactionSpecification.accountNameContains(criteria.searchTerm()))
                        .or(TransactionSpecification.tagNameContains(criteria.searchTerm()))
                        .and(TransactionSpecification.userEquals(user))
                        .and(TransactionSpecification.nonDeleted(criteria.nonDeleted()))
                        .and(TransactionSpecification.sinceDate(criteria.sinceDate()))
                        .and(TransactionSpecification.untilDate(criteria.untilDate()))
                        .and(TransactionSpecification.accountIdsIn(criteria.accountIds()))
                        .and(TransactionSpecification.categoryIdsIn(criteria.categoryIds()))
                        .and(TransactionSpecification.tagIdsIn(criteria.tagIds())));
        return findAll(specification, pageable);
    }
}
