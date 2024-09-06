package com.budgetapi.factories;

import com.budgetapi.account.model.Account;
import com.budgetapi.user.model.User;

import java.util.function.Consumer;

public class AccountFactory {

    private static final String DEFAULT_NAME = "Nubank";
    private static final String DEFAULT_CURRENCY = "BRL";

    private AccountFactory() {
    }

    public static Account createAccount(User user) {
        return createAccount(user, builder -> {
        });
    }

    public static Account createDeletedAccount(User user) {
        return createAccount(user, builder -> builder.deleted(true));
    }

    public static Account createAccount(User user, Consumer<Account.AccountBuilder> customizer) {
        Account.AccountBuilder builder = Account.builder()
                .name(DEFAULT_NAME)
                .user(user)
                .currency(DEFAULT_CURRENCY);

        customizer.accept(builder);

        return builder.build();
    }
}
