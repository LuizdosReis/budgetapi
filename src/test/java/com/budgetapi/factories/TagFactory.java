package com.budgetapi.factories;

import com.budgetapi.tag.model.Tag;
import com.budgetapi.user.model.User;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class TagFactory {

    private static final String DEFAULT_NAME = "tag_name";

    public static Tag create(User user) {
        return create(user, builder -> {
        });
    }

    public static Tag create(User user, Consumer<Tag.TagBuilder> customizer) {
        Tag.TagBuilder builder = Tag.builder()
                .name(DEFAULT_NAME)
                .user(user);

        customizer.accept(builder);

        return builder.build();
    }

    public static Tag createDelete(User user, Consumer<Tag.TagBuilder> customizer) {
        return create(user, customizer.andThen(c -> c.deleted(true)));
    }
}
