package com.budgetapi.transactions.repository;

import com.budgetapi.EnableTestcontainers;
import com.budgetapi.account.model.Account;
import com.budgetapi.auditing.AuditingConfig;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.specification.TransactionSpecification;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditingConfig.class)
@EnableTestcontainers
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository repository;

    private User user;
    private Transaction transaction;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        user = entityManager.persist(UserFactory.createUser());
        transaction = createTransaction(user, c -> c.description("Transaction").amount(BigDecimal.valueOf(5.50)));
        transaction2 = createTransaction(user, c -> c.description("transaction2"));
        transaction3 = createDeletedTransaction(user, c -> c.description("transaction3"));
        entityManager.flush();
    }

    @Test
    @DisplayName("save should set created date and not set updated date")
    void save_shouldSetCreatedDateAndNotSetUpdatedDate() {
        entityManager.detach(transaction);

        Transaction loadTransaction = entityManager.find(Transaction.class, transaction.getId());

        assertThat(loadTransaction.getCreateDate()).isNotNull();
        assertThat(loadTransaction.getModifiedDate()).isNull();
    }

    @Test
    @DisplayName("update should not change created date and set updated date")
    void update_shouldNotChangeCreatedDateAndSetUpdatedDate() {
        LocalDateTime createDate = transaction.getCreateDate();

        transaction.setDescription("updatedDescription");

        repository.save(transaction);
        entityManager.flush();
        entityManager.detach(transaction);

        Transaction loadTransaction = entityManager.find(Transaction.class, transaction.getId());

        assertThat(loadTransaction.getCreateDate()).isCloseTo(createDate, byLessThan(1, ChronoUnit.MICROS));
        assertThat(loadTransaction.getModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return when exists")
    void findByIdAndAccountUser_shouldReturnWhenExists() {
        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(transaction.getId(), user);

        assertThat(transactionOptional).isPresent().contains(transaction);
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return empty when id does not exists")
    void findByIdAndAccountUser_shouldReturnEmptyWhenIdDoesNotExists() {
        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(new TransactionId(), user);

        assertThat(transactionOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return empty when transaction belongs to other user")
    void findByIdAndUser_shouldReturnEmptyWhenTransactionBelongsToOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser(c -> c.username("otherUser")));
        Account otherUserAccount = entityManager.persist(AccountFactory.createAccount(otherUser));
        Category otherUserCategory = entityManager.persist(CategoryFactory.create(otherUser));
        Transaction otherUsertransaction = entityManager.persist(TransactionFactory.create(otherUserAccount, otherUserCategory));

        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(otherUsertransaction.getId(), user);
        assertThat(transactionOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return empty when other user tries to access transaction")
    void findByIdAndAccountUser_shouldReturnEmptyWhenOtherUserTriesToAccessCategory() {
        User otherUser = entityManager.persist(UserFactory.createUser(c -> c.username("otherUser")));

        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(transaction.getId(), otherUser);
        assertThat(transactionOptional).isEmpty();
    }

    @Test
    @DisplayName("delete should set deleted to true")
    void delete_shouldSetDeletedToTrue() {
        repository.delete(transaction);
        entityManager.flush();

        Transaction found = repository.findById(transaction.getId()).orElseThrow();
        assertThat(found.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("findAll by description should return transactions with exact description match")
    void findAllByDescription_shouldReturnTransactionsWithExactDescriptionMatch() {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.descriptionContains("transaction2"), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction2);
    }

    @Test
    @DisplayName("findAll by description should return transactions with description containing substring 'trans'")
    void findAllByDescription_shouldReturnTransactionsWithDescriptionContainingTransSubstring() {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.descriptionContains("trans"), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findAll by description should return transactions with description containing substring 'action3'")
    void findAllByDescription_shouldReturnTransactionsWithDescriptionContainingAction3Substring() {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.descriptionContains("action3"), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction3);
    }

    @Test
    @DisplayName("findAll by user should return transactions from user")
    void findAllByUser_shouldReturnTransactionsFromUser() {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.userEquals(user), Pageable.unpaged());
        assertThat(transactions)
                .hasSize(3)
                .containsExactlyInAnyOrder(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findAll by user should return transactions from other user")
    void findAllByUser_shouldNotReturnTransactionsFromOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser(c -> c.username("otherUser")));
        createTransaction(otherUser);

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.userEquals(user), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findAll isActive should not return active transactions")
    void findAllIsActive_shouldReturnActiveTransactions() {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.isActive(), Pageable.unpaged());
        assertThat(transactions)
                .hasSize(2)
                .containsExactlyInAnyOrder(transaction, transaction2);
    }

    @Test
    @DisplayName("findAll should return all transactions including deleted")
    void findAll_shouldReturnAllTransactionsIncludingDeleted() {
        Page<Transaction> transactions = repository.findAll(null, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(3)
                .containsExactlyInAnyOrder(transaction, transaction2, transaction3);
    }

    @ParameterizedTest
    @DisplayName("findAll by amount should return transactions with amount containing the substring")
    @ValueSource(strings = {"5.50", "5", "50"})
    void findAllByAmount_shouldReturnTransactionsWithAmountContainingSubstring(String substring) {
        Page<Transaction> transactions = repository.findAll(TransactionSpecification.amountContains(substring), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction);
    }

    @ParameterizedTest
    @DisplayName("findAll by category name should return transactions with category name containing the substring")
    @ValueSource(strings = {"Expense", "Exp", "pen", "nse"})
    void findAllByCategoryName_shouldReturnTransactionsWithCategoryNameContainingSubstring(String substring) {
        Category category = CategoryFactory.create(user, c -> c.name("Expense"));
        entityManager.persist(category);
        Transaction expenseTransaction = createTransaction(user, c -> c.category(category));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.categoryNameContains(substring), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expenseTransaction);
    }

    @ParameterizedTest
    @DisplayName("findAll by account name should return transactions with account name containing the substring")
    @ValueSource(strings = {"ActiveBank", "iveBa", "Act", "eBank"})
    void findAllByAccountName_shouldReturnTransactionsWithAccountNameContaining(String substring) {
        Account account = AccountFactory.createAccount(user, c -> c.name("ActiveBank"));
        entityManager.persist(account);
        Transaction expectedTransaction = createTransaction(user, c -> c.account(account));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.accountNameContains(substring), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    @ParameterizedTest
    @DisplayName("findAll by tag name should return transactions with tag name containing the substring")
    @ValueSource(strings = {"Weekend", "eeke", "wee", "end"})
    void findAllByTagName_shouldReturnTransactionsWithTagNameContaining(String substring) {
        Tag tag = TagFactory.create(user, c -> c.name("Weekend"));
        entityManager.persist(tag);
        Transaction expectedTransaction = createTransaction(user, c -> c.tags(Set.of(tag)));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.tagNameContains(substring), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    @ParameterizedTest
    @MethodSource("dateGreaterThanOrEqualTo")
    @DisplayName("findAll with date greater than or equal to given date return transaction")
    void findAll_withDateGreaterThanOrEqualTo_shouldReturnTransactions(LocalDate date) {
        Transaction expectedTransaction = createTransaction(user, c -> c.date(LocalDate.of(2025, Month.FEBRUARY, 20)));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.withDateGreaterThanOrEqualTo(date), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    private static Stream<LocalDate> dateGreaterThanOrEqualTo() {
        return Stream.of(
                LocalDate.of(2025, Month.FEBRUARY, 19),
                LocalDate.of(2025, Month.JANUARY, 20)
        );
    }

    @ParameterizedTest
    @MethodSource("dateLessThanOrEqualTo")
    @DisplayName("findAll with date less than or equal to given date return transaction")
    void findAll_withDateLessThanOrEqualTo_shouldReturnTransactions(LocalDate date) {
        Transaction expectedTransaction = createTransaction(user, c -> c.date(LocalDate.of(2019, Month.FEBRUARY, 20)));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.withDateLessThanOrEqualTo(date), Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    private static Stream<LocalDate> dateLessThanOrEqualTo() {
        return Stream.of(
                LocalDate.of(2019, Month.OCTOBER, 8),
                LocalDate.of(2019, Month.FEBRUARY, 20)
        );
    }

    @Test
    @DisplayName("findAll by account ids in should return transactions with account ids on the list")
    void findAllByAccountIdsIn_shouldReturnTransactionsWithAccountIdsIn() {
        Account firstAccount = AccountFactory.createAccount(user);
        Account secondAccount = AccountFactory.createAccount(user);
        entityManager.persist(firstAccount);
        entityManager.persist(secondAccount);
        Transaction firstTransaction = createTransaction(user, c -> c.account(firstAccount));
        Transaction secondTransaction = createTransaction(user, c -> c.account(secondAccount));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.accountIdsIn(Set.of(firstAccount.getId(), secondAccount.getId())), Pageable.unpaged());
        assertThat(transactions).containsExactlyInAnyOrder(firstTransaction, secondTransaction);
    }

    @Test
    @DisplayName("findAll by category ids in should return transactions with category ids on the list")
    void findAllByCategoryIdsIn_shouldReturnTransactionsWithCategoryIdsIn() {
        Category firstCategory = CategoryFactory.create(user);
        Category secondCategory = CategoryFactory.create(user);
        entityManager.persist(firstCategory);
        entityManager.persist(secondCategory);
        Transaction firstTransaction = createTransaction(user, c -> c.category(firstCategory));
        Transaction secondTransaction = createTransaction(user, c -> c.category(secondCategory));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.categoryIdsIn(Set.of(firstCategory.getId(), secondCategory.getId())), Pageable.unpaged());
        assertThat(transactions).containsExactlyInAnyOrder(firstTransaction, secondTransaction);
    }

    @Test
    @DisplayName("findAll by tag ids in should return transactions with tag ids on the list")
    void findAllByTagIdsIn_shouldReturnTransactionsWithTagIdsIn() {
        Tag firstTag = TagFactory.create(user, c -> c.name("Weekend"));
        Tag secondTag = TagFactory.create(user, c -> c.name("Restaurant"));
        entityManager.persist(firstTag);
        entityManager.persist(secondTag);
        Transaction firstTransaction = createTransaction(user, c -> c.tags(Set.of(firstTag)));
        Transaction secondTransaction = createTransaction(user, c -> c.tags(Set.of(secondTag)));

        Page<Transaction> transactions = repository.findAll(TransactionSpecification.tagIdsIn(Set.of(firstTag.getId(), secondTag.getId())), Pageable.unpaged());
        assertThat(transactions).containsExactlyInAnyOrder(firstTransaction, secondTransaction);
    }

    private Transaction createTransaction(User user) {
        return createTransaction(user, c -> {
        });
    }

    private Transaction createTransaction(User user, Consumer<Transaction.TransactionBuilder> customizer) {
        Account account = entityManager.persist(AccountFactory.createAccount(user));
        Category category = entityManager.persist(CategoryFactory.create(user));
        entityManager.persist(account);
        entityManager.persist(category);
        return entityManager.persist(TransactionFactory.create(account, category, customizer));
    }

    private Transaction createDeletedTransaction(User user, Consumer<Transaction.TransactionBuilder> customizer) {
        Account account = entityManager.persist(AccountFactory.createAccount(user));
        Category category = entityManager.persist(CategoryFactory.create(user));
        entityManager.persist(account);
        entityManager.persist(category);
        return entityManager.persist(TransactionFactory.createDeleted(account, category, customizer));
    }
}
