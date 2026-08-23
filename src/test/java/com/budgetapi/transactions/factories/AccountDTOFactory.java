package com.budgetapi.transactions.factories;

import com.budgetapi.transaction.dto.AccountDTO;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class AccountDTOFactory {

    private static final String DEFAULT_NAME = "account";
    private static final String DEFAULT_CURRENCY = "EUR";

    public static AccountDTO create() {
        return create(builder -> {
        });
    }

    public static AccountDTO create(Consumer<AccountDTOBuilder> customizer) {
        AccountDTOBuilder builder = new AccountDTOBuilder()
                .id(UUID.randomUUID())
                .name(DEFAULT_NAME)
                .currency(DEFAULT_CURRENCY);

        customizer.accept(builder);
        return builder.build();
    }

    @Builder()
    private static AccountDTO build(
            UUID id,
            String name,
            String currency
    ) {
        return new AccountDTO(id, name, currency);
    }
}