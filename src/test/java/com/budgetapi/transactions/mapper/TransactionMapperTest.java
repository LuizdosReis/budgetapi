package com.budgetapi.transactions.mapper;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {


    @Test
    @DisplayName("toModel should create tag with correct fields")
    void toModel_shouldCreateTransactionWithCorrectFields() {
        User user = UserFactory.createUser();
        Account account = AccountFactory.createAccount(user);
        Category category = CategoryFactory.create(user);
        Tag tag = TagFactory.create(user);
        TransactionRequestDTO dto = new TransactionRequestDTO("transaction", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        Transaction transaction = TransactionMapper.MAPPER.toModel(dto, account, category, Set.of(tag));

        assertThat(transaction.getDescription()).isEqualTo(dto.description());
        assertThat(transaction.getAmount()).isEqualTo(dto.amount());
        assertThat(transaction.getDate()).isEqualTo(dto.date());
        assertThat(transaction.getAccount()).isEqualTo(account);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getTags()).isEqualTo(Set.of(tag));
        assertThat(transaction.getStatus()).isEqualTo(dto.status());
        assertThat(transaction.getId()).isNull();
        assertThat(transaction.isDeleted()).isFalse();
    }
}
