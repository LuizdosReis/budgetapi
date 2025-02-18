package com.budgetapi.transaction.specification;

import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.user.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class TransactionSpecification {

    public static Specification<Transaction> descriptionContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("description").get("value")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> userEquals(User user) {
        return (root, query, criteriaBuilder) ->
                user == null ? null : criteriaBuilder.equal(root.get("account").get("user"), user);
    }

    public static Specification<Transaction> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }

    public static Specification<Transaction> amountContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.toString(root.get("amount")), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> categoryNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get("category").get("name"))), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> accountNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get("account").get("name"))), "%" + substring.toLowerCase() + "%");
    }

    public static Specification<Transaction> tagNameContains(String substring) {
        return (root, query, criteriaBuilder) ->
                substring == null ? null : criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get("tags").get("name"))), "%" + substring.toLowerCase() + "%");
    }
}
