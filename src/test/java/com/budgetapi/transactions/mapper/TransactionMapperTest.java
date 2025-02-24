package com.budgetapi.transactions.mapper;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.dto.TagDTO;
import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final User user = UserFactory.createUser();
    private final Account account = AccountFactory.createAccount(user);
    private final Category category = CategoryFactory.create(user);
    private final Tag tag = TagFactory.create(user);


    @Test
    @DisplayName("toModel should create tag with correct fields")
    void toModel_shouldCreateTransactionWithCorrectFields() {
        TransactionRequestDTO dto = new TransactionRequestDTO("transaction", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        Transaction transaction = TransactionMapper.MAPPER.toModel(dto, account, category, Set.of(tag));

        assertThat(transaction.getDescription()).isEqualTo(dto.description());
        assertThat(transaction.getAmount()).isEqualTo(dto.amount());
        assertThat(transaction.getDate()).isEqualTo(dto.date());
        assertThat(transaction.getAccount()).isEqualTo(account);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getTags()).isEqualTo(Set.of(tag));
        assertThat(transaction.getStatus()).isEqualTo(dto.status());
        assertThat(transaction.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("updateModel should update with correct fields")
    void updateModel_shouldUpdateWithCorrectFields() {
        TransactionRequestDTO dto = new TransactionRequestDTO("transaction", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.of(2022, Month.DECEMBER, 12), TransactionStatus.SCHEDULED);
        Transaction transaction = TransactionFactory.create(account, category);

        Account newAccount = AccountFactory.createAccount(user);
        Category newCategory = CategoryFactory.create(user);

        TransactionMapper.MAPPER.updateModel(dto, newAccount, newCategory, Set.of(tag), transaction);

        assertThat(transaction.getDescription()).isEqualTo(dto.description());
        assertThat(transaction.getAmount()).isEqualTo(dto.amount());
        assertThat(transaction.getDate()).isEqualTo(dto.date());
        assertThat(transaction.getAccount()).isEqualTo(newAccount);
        assertThat(transaction.getCategory()).isEqualTo(newCategory);
        assertThat(transaction.getTags()).isEqualTo(Set.of(tag));
        assertThat(transaction.getStatus()).isEqualTo(dto.status());
        assertThat(transaction.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("toDTO should create DTO with correct fields")
    void toDTO_shouldCreateDTOWithCorrectFields() {
        Transaction transaction = TransactionFactory.create(account, category, c -> c.tags(Set.of(tag)));

        TransactionDTO dto = TransactionMapper.MAPPER.toDTO(transaction);

        assertThat(dto.id()).isEqualTo(transaction.getId());
        assertThat(dto.description()).isEqualTo(transaction.getDescription());
        assertThat(dto.amount()).isEqualTo(transaction.getAmount());
        assertThat(dto.date()).isEqualTo(transaction.getDate());
        assertThat(dto.account().name()).isEqualTo(account.getName());
        assertThat(dto.account().currency()).isEqualTo(account.getCurrency());
        assertThat(dto.account().id()).isEqualTo(account.getId());
        assertThat(dto.category().name()).isEqualTo(category.getName());
        assertThat(dto.category().id()).isEqualTo(category.getId());
        assertThat(dto.category().type()).isEqualTo(category.getType().toString());
        assertThat(dto.tags()).isEqualTo(Set.of(new TagDTO(tag.getId(), tag.getName())));
        assertThat(dto.status()).isEqualTo(transaction.getStatus());
        assertThat(dto.deleted()).isEqualTo(transaction.isDeleted());
    }
}
