package com.budgetapi.factories;

import com.budgetapi.user.model.User;

import java.util.function.Consumer;

public class UserFactory {

    private UserFactory() {
    }

    public static User createUser(Consumer<User.UserBuilder> customizer) {
        User.UserBuilder builder = User.builder()
                .password("$2a$10$smU2UmQgtZ2wObOMhcAr0.MhNN6tWv/Q38JmGev6JmDZZ50a1xfJ2")
                .roles("USER");

        customizer.accept(builder);

        return builder.build();
    }

    public static User createUser() {
        return createUser(builder -> builder.username("user" + Math.random()));
    }
}
