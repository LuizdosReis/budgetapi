package com.budgetapi.transactions.factories;

import com.budgetapi.transaction.dto.TagDTO;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class TagDTOFactory {

    private static final String DEFAULT_NAME = "tag";

    public static TagDTO create() {
        return create(builder -> {
        });
    }

    public static TagDTO create(Consumer<TagDTOBuilder> customizer) {
        TagDTOBuilder builder = new TagDTOBuilder()
                .id(UUID.randomUUID())
                .name(DEFAULT_NAME);

        customizer.accept(builder);
        return builder.build();
    }

    @Builder()
    private static TagDTO build(
            UUID id,
            String name
    ) {
        return new TagDTO(id, name);
    }
}