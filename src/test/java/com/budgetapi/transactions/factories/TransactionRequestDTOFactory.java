package com.budgetapi.transactions.factories;

import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.TransactionStatus;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public class TransactionRequestDTOFactory {

    private static final String DEFAULT_DESCRIPTION = "description";
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.TEN;
    private static final LocalDate DEFAULT_DATE =
            LocalDate.of(2019, Month.OCTOBER, 9);

    public static TransactionRequestDTO create() {
        return create(builder -> {
        });
    }

    public static TransactionRequestDTO create(Consumer<TransactionRequestDTOBuilder> customizer) {
        TransactionRequestDTOBuilder builder = new TransactionRequestDTOBuilder()
                .description(DEFAULT_DESCRIPTION)
                .accountID(UUID.randomUUID())
                .categoryID(UUID.randomUUID())
                .tagIDs(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                .amount(DEFAULT_AMOUNT)
                .id(UUID.randomUUID())
                .date(DEFAULT_DATE)
                .status(TransactionStatus.REGISTERED)
                .direction(Direction.OUT);

        customizer.accept(builder);

        return builder.build();
    }

    @Builder()
    private static TransactionRequestDTO build(
            String description,
            UUID accountID,
            UUID categoryID,
            Set<UUID> tagIDs,
            BigDecimal amount,
            UUID id,
            LocalDate date,
            TransactionStatus status,
            Direction direction
    ) {
        return new TransactionRequestDTO(description, accountID, categoryID, tagIDs, amount, date, status, direction);
    }
}