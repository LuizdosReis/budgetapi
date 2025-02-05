package com.budgetapi.factories;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.function.Consumer;

@UtilityClass
public class TransactionFactory {

    private static final String DEFAULT_DESCRIPTION = "description";
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(100L);

    public static Transaction create(Account account, Category category) {
        return create(account, category, builder -> {
        });
    }

    public static Transaction create(Account account, Category category, Consumer<Transaction.TransactionBuilder> customizer) {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .description(DEFAULT_DESCRIPTION)
                .account(account)
                .category(category)
                .amount(DEFAULT_AMOUNT)
                .date(LocalDate.now())
                .status(TransactionStatus.REGISTERED)
                .tags(Set.of());

        customizer.accept(builder);

        return builder.build();
    }

    public static Transaction createDeleted(Account account, Category category) {
        return createDeleted(account, category, builder -> {
        });
    }

    public static Transaction createDeleted(Account account, Category category, Consumer<Transaction.TransactionBuilder> customizer) {
        return create(account, category, customizer.andThen(c -> c.deleted(true)));
    }
}
