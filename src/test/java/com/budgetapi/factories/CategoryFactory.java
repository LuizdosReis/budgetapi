package com.budgetapi.factories;

import com.budgetapi.category.model.Category;
import com.budgetapi.category.model.Type;
import com.budgetapi.user.model.User;

import java.util.function.Consumer;

public class CategoryFactory {

    private static final String DEFAULT_NAME = "Category";

    private CategoryFactory() {
    }

    public static Category create(User user) {
        return create(user, builder -> {
        });
    }

    public static Category create(User user, Consumer<Category.CategoryBuilder> customizer) {
        Category.CategoryBuilder builder = Category.builder()
                .name(DEFAULT_NAME)
                .user(user)
                .type(Type.EXPENSE);

        customizer.accept(builder);

        return builder.build();
    }

    public static Category createDelete(User user) {
        return create(user, builder -> builder.deleted(true));
    }
}
