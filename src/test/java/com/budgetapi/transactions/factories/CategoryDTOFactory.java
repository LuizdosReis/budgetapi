package com.budgetapi.transactions.factories;

import com.budgetapi.transaction.dto.CategoryDTO;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class CategoryDTOFactory {

    private static final String DEFAULT_NAME = "category";
    private static final String DEFAULT_TYPE = "type";

    public static CategoryDTO create() {
        return create(builder -> {
        });
    }

    public static CategoryDTO create(Consumer<CategoryDTOBuilder> customizer) {
        CategoryDTOBuilder builder = new CategoryDTOBuilder()
                .id(UUID.randomUUID())
                .name(DEFAULT_NAME)
                .type(DEFAULT_TYPE);

        customizer.accept(builder);
        return builder.build();
    }

    @Builder()
    private static CategoryDTO build(
            UUID id,
            String name,
            String type
    ) {
        return new CategoryDTO(id, name, type);
    }
}