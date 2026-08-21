package com.budgetapi.transactions.model;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    private final User user = UserFactory.createUser();
    private final Account account = AccountFactory.createAccount(user);
    private final Category category = CategoryFactory.create(user);
    private final Set<Tag> tags = Set.of(TagFactory.create(user));
    private final String description = "description";
    private final BigDecimal amount = BigDecimal.valueOf(100);
    private final LocalDate date = LocalDate.now();
    private final TransactionStatus status = TransactionStatus.REGISTERED;
    private final Direction direction = Direction.OUT;

    @Test
    @DisplayName("Create Transaction")
    void createTransaction() {
        Transaction transaction = Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build();
        assertThat(transaction.getDescription()).isEqualTo(description);
        assertThat(transaction.getAccount()).isEqualTo(account);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getTags()).isEqualTo(tags);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getDate()).isEqualTo(date);
        assertThat(transaction.getStatus()).isEqualTo(status);
        assertThat(transaction.isDeleted()).isFalse();
        assertThat(transaction.getDirection()).isEqualTo(direction);
    }

    @Test
    @DisplayName("Do not create transaction with null description")
    void doNotCreateTransactionWithNullDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Description must have text");
    }

    @Test
    @DisplayName("Do not create transaction with null account")
    void doNotCreateTransactionWithNullAccount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .category(category)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Account must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null category")
    void doNotCreateTransactionWithNullCategory() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Category must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null tags")
    void doNotCreateTransactionWithNullTags() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .status(status)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Tags must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null amount")
    void doNotCreateTransactionWithNullAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Amount must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null date")
    void doNotCreateTransactionWithNullDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Date must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null status")
    void doNotCreateTransactionWithNullStatus() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Status must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with null status")
    void doNotCreateTransactionWithNullDirection() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .tags(tags)
                .status(status)
                .build());
        assertThat(exception.getMessage()).contains("Direction must not be null");
    }

    @Test
    @DisplayName("Do not create transaction with category from other user")
    void doNotCreateTransactionWithCategoryFromOtherUser() {
        User otherUser = UserFactory.createUser();
        Category otherUserCategory = CategoryFactory.create(otherUser);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(otherUserCategory)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(tags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Account user is not the same as category user");
    }

    @Test
    @DisplayName("Do not create transaction with tag from other user")
    void doNotCreateTransactionWithTagFromOtherUser() {
        User otherUser = UserFactory.createUser();
        Tag otherUserTag = TagFactory.create(otherUser);
        Tag tag = TagFactory.create(user);
        Set<Tag> otherUserTags = Set.of(tag, otherUserTag);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .status(status)
                .tags(otherUserTags)
                .direction(direction)
                .build());
        assertThat(exception.getMessage()).contains("Tags user is not the same as account user");
    }

    @Test
    @DisplayName("Create transaction with empty tags")
    void createTransactionWithEmptyTags() {
        Transaction transaction = Transaction.builder()
                .description(description)
                .account(account)
                .category(category)
                .amount(amount)
                .date(date)
                .tags(Set.of())
                .status(status)
                .direction(direction)
                .build();
        assertThat(transaction).isNotNull();
    }
}
