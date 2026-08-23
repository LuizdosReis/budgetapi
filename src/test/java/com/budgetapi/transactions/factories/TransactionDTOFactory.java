package com.budgetapi.transactions.factories;

import com.budgetapi.transaction.dto.AccountDTO;
import com.budgetapi.transaction.dto.CategoryDTO;
import com.budgetapi.transaction.dto.TagDTO;
import com.budgetapi.transaction.dto.TransactionDTO;
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
public class TransactionDTOFactory {

    private static final String DEFAULT_DESCRIPTION = "description";
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.TEN;
    private static final LocalDate DEFAULT_DATE =
            LocalDate.of(2019, Month.OCTOBER, 9);

    public static TransactionDTO create() {
        return create(builder -> {
        });
    }

    public static TransactionDTO create(Consumer<TransactionDTOBuilder> customizer) {
        TransactionDTOBuilder builder = new TransactionDTOBuilder()
                .description(DEFAULT_DESCRIPTION)
                .account(AccountDTOFactory.create())
                .category(CategoryDTOFactory.create())
                .tags(Set.of(TagDTOFactory.create(), TagDTOFactory.create()))
                .amount(DEFAULT_AMOUNT)
                .id(UUID.randomUUID())
                .date(DEFAULT_DATE)
                .status(TransactionStatus.REGISTERED)
                .deleted(false)
                .direction(Direction.OUT);

        customizer.accept(builder);

        return builder.build();
    }

    @Builder()
    private static TransactionDTO build(
            String description,
            AccountDTO account,
            CategoryDTO category,
            Set<TagDTO> tags,
            BigDecimal amount,
            UUID id,
            LocalDate date,
            TransactionStatus status,
            boolean deleted,
            Direction direction
    ) {
        return new TransactionDTO(description, account, category, tags, amount, id, date, status, deleted, direction);
    }
}