package com.budgetapi.factories;

import com.budgetapi.user.model.User;

public class UserFactory {

    private UserFactory() {
    }

    public static User createUser(String username) {
        return User.builder()
                .username(username)
                .password("$2a$10$smU2UmQgtZ2wObOMhcAr0.MhNN6tWv/Q38JmGev6JmDZZ50a1xfJ2")
                .roles("USER")
                .build();
    }

    public static User createUser() {
        return createUser("user");
    }
}
