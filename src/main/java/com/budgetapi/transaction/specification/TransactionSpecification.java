package com.budgetapi.transaction.specification;

import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.user.model.User;
import jakarta.persistence.criteria.JoinType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class TransactionSpecification {

    public static Specification<Transaction> descriptionContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("description").get("value")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> userEquals(User user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("account").get("user"), user);
    }

    public static Specification<Transaction> nonDeleted(boolean onlyNonDeleted) {
        return (root, query, criteriaBuilder) ->
                !onlyNonDeleted ? null : criteriaBuilder.isFalse(root.get("deleted"));
    }

    public static Specification<Transaction> amountContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.toString(root.get("amount")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> categoryNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> accountNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("account").get("name")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> tagNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.join("tags", JoinType.LEFT).get("name"))), "%" + substring.toLowerCase() + "%");
    }


    public static Specification<Transaction> sinceDate(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("date"), date);
    }

    public static Specification<Transaction> untilDate(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("date"), date);
    }

    public static Specification<Transaction> accountIdsIn(Set<UUID> accountIds) {
        return (root, query, criteriaBuilder) ->
                accountIds == null || accountIds.isEmpty() ? null : criteriaBuilder.in(root.get("account").get("id")).value(accountIds);
    }

    public static Specification<Transaction> categoryIdsIn(Set<UUID> categoryIds) {
        return (root, query, criteriaBuilder) ->
                categoryIds == null || categoryIds.isEmpty() ? null : criteriaBuilder.in(root.get("category").get("id")).value(categoryIds);
    }

    public static Specification<Transaction> tagIdsIn(Set<UUID> tagIds) {
        return (root, query, criteriaBuilder) ->
                tagIds == null || tagIds.isEmpty() ? null : criteriaBuilder.in(root.get("tags").get("id")).value(tagIds);
    }
}