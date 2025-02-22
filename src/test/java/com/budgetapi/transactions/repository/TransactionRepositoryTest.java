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
import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.repository.TransactionRepository;
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
    @DisplayName("findAllBy should return transactions with description containing exact searchTerm match")
    void findAllBy_shouldReturnTransactionsWithDescriptionContainingESearchTermMatch() {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm("transaction2").build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction2);
    }

    @Test
    @DisplayName("findAllBy should return transactions with description containing substring 'trans'")
    void findAllBy_shouldReturnTransactionsWithDescriptionContainingTransSubstring() {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm("trans").build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findAllBy should return transactions with description containing substring 'action3'")
    void findAllBy_shouldReturnTransactionsWithDescriptionContainingAction3Substring() {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm("action3").build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction3);
    }

    @Test
    @DisplayName("findAllBy should not return transactions from other user")
    void findAllBy_shouldNotReturnTransactionsFromOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser(c -> c.username("otherUser")));
        createTransaction(otherUser);

        TransactionSearchCriteria criteria = TransactionSearchCriteria
                .builder()
                .accountIds(Set.of())
                .tagIds(Set.of())
                .categoryIds(Set.of())
                .build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findAllBy should not return deleted transactions")
    void findAllBy_shouldNotReturnDeletedTransactions() {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().nonDeleted(true).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(2)
                .containsExactlyInAnyOrder(transaction, transaction2);
    }

    @Test
    @DisplayName("findAllBy should return all transactions including deleted")
    void findAllBy_shouldReturnAllTransactionsIncludingDeleted() {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().nonDeleted(false).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(3)
                .containsExactlyInAnyOrder(transaction, transaction2, transaction3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"5.50", "5", "50"})
    @DisplayName("findAllBy should return transactions with amount containing searchTerm")
    void findAllBy_shouldReturnTransactionsWithAmountContainingSearchTerm(String searchTerm) {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm(searchTerm).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(transaction);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Expense", "Exp", "pen", "nse"})
    @DisplayName("findAllBy should return transactions with category name containing searchTerm")
    void findAllBy_shouldReturnTransactionsWithCategoryNameContainingSearchTerm(String searchTerm) {
        Category category = CategoryFactory.create(user, c -> c.name("Expense"));
        entityManager.persist(category);
        Transaction expectedTransaction = createTransaction(user, c -> c.category(category));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm(searchTerm).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Milennium", "ium", "Mil", "lenn"})
    @DisplayName("findAll should return transactions with account name containing searchTerm")
    void findAllBy_shouldReturnTransactionsWithAccountNameContainingSearchTerm(String searchTerm) {
        Account account = AccountFactory.createAccount(user, c -> c.name("Milennium"));
        entityManager.persist(account);
        Transaction expectedTransaction = createTransaction(user, c -> c.account(account));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm(searchTerm).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Weekend", "eeke", "wee", "end"})
    @DisplayName("findAllBy should return transactions with tag name containing SearchTerm")
    void findAllBy_shouldReturnTransactionsWithTagNameContainingSearchTerm(String searchTerm) {
        Tag tag = TagFactory.create(user, c -> c.name("Weekend"));
        entityManager.persist(tag);
        Transaction expectedTransaction = createTransaction(user, c -> c.tags(Set.of(tag)));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().searchTerm(searchTerm).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions)
                .containsExactly(expectedTransaction);
    }

    @ParameterizedTest
    @MethodSource("dateGreaterThanOrEqualTo")
    @DisplayName("findAllBy should return transactions with date greater than or equal to given sinceDate")
    void findAllBy_shouldReturnTransactionWithDateGreaterThanOrEqualToGivenSinceDate(LocalDate sinceDate) {
        Transaction expectedTransaction = createTransaction(user, c -> c.date(LocalDate.of(2025, Month.FEBRUARY, 20)));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().sinceDate(sinceDate).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
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
    @DisplayName("findAllBy should return transactions with date less than or equal to given until date")
    void findAllBy_shouldReturnTransactionsWithDateLessThanOrEqualToGivenUntilDate(LocalDate untilDate) {
        Transaction expectedTransaction = createTransaction(user, c -> c.date(LocalDate.of(2019, Month.FEBRUARY, 20)));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().untilDate(untilDate).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
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
    @DisplayName("findAllBy should return transactions with account ids in")
    void findAllBy_shouldReturnTransactionsWithAccountIdsIn() {
        Account firstAccount = AccountFactory.createAccount(user);
        Account secondAccount = AccountFactory.createAccount(user);
        entityManager.persist(firstAccount);
        entityManager.persist(secondAccount);
        Transaction firstTransaction = createTransaction(user, c -> c.account(firstAccount));
        Transaction secondTransaction = createTransaction(user, c -> c.account(secondAccount));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().accountIds(Set.of(firstAccount.getId(), secondAccount.getId())).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions).containsExactlyInAnyOrder(firstTransaction, secondTransaction);
    }

    @Test
    @DisplayName("findAllBy should return transactions with category ids in")
    void findAllBy_shouldReturnTransactionsWithCategoryIdsIn() {
        Category firstCategory = CategoryFactory.create(user);
        Category secondCategory = CategoryFactory.create(user);
        entityManager.persist(firstCategory);
        entityManager.persist(secondCategory);
        Transaction firstTransaction = createTransaction(user, c -> c.category(firstCategory));
        Transaction secondTransaction = createTransaction(user, c -> c.category(secondCategory));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().categoryIds(Set.of(firstCategory.getId(), secondCategory.getId())).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
        assertThat(transactions).containsExactlyInAnyOrder(firstTransaction, secondTransaction);
    }

    @Test
    @DisplayName("findAllBy should return transactions with tag ids in")
    void findAllBy_shouldReturnTransactionsWithTagIdsIn() {
        Tag firstTag = TagFactory.create(user, c -> c.name("Weekend"));
        Tag secondTag = TagFactory.create(user, c -> c.name("Restaurant"));
        entityManager.persist(firstTag);
        entityManager.persist(secondTag);
        Transaction firstTransaction = createTransaction(user, c -> c.tags(Set.of(firstTag)));
        Transaction secondTransaction = createTransaction(user, c -> c.tags(Set.of(secondTag)));

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().tagIds(Set.of(firstTag.getId(), secondTag.getId())).build();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, Pageable.unpaged());
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
