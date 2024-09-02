package com.budgetapi.factories;

import com.budgetapi.account.model.Account;
import com.budgetapi.user.model.User;

public class AccountFactory {

    private AccountFactory() {
    }

    public static Account createAccount(User user) {
        return Account.builder()
                .name("Nubank")
                .user(user)
                .currency("BRL")
                .build();
    }
}
