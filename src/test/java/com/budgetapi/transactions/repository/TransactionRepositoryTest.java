package com.budgetapi.transactions.repository;

import com.budgetapi.account.model.Account;
import com.budgetapi.auditing.AuditingConfig;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditingConfig.class)
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
        transaction = createTransaction(user);
        transaction2 = createTransaction(user);
        transaction3 = createDeletedTransaction(user);
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
        UUID nonExistentId = UUID.randomUUID();
        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(nonExistentId, user);

        assertThat(transactionOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return empty when transaction belongs to other user")
    void findByIdAndUser_shouldReturnEmptyWhenTransactionBelongsToOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser());
        Account otherUserAccount = entityManager.persist(AccountFactory.createAccount(otherUser));
        Category otherUserCategory = entityManager.persist(CategoryFactory.create(otherUser));
        Transaction otherUsertransaction = entityManager.persist(TransactionFactory.create(otherUserAccount, otherUserCategory));

        Optional<Transaction> transactionOptional = repository.findByIdAndAccountUser(otherUsertransaction.getId(), user);
        assertThat(transactionOptional).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndAccountUser should return empty when other user tries to access transaction")
    void findByIdAndAccountUser_shouldReturnEmptyWhenOtherUserTriesToAccessCategory() {
        User otherUser = entityManager.persist(UserFactory.createUser());

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
    @DisplayName("findByAccountUser should return all transactions")
    void findByAccountUser_shouldReturnAllTransactions() {
        Page<Transaction> transactions = repository.findByAccountUser(user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(3)
                .containsExactlyInAnyOrder(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findByAccountUserAndDeletedFalse should not return deleted transactions")
    void findByAccountUserAndDeletedFalse_shouldReturnNotDeletedTransactions() {
        Page<Transaction> transactions = repository.findByAccountUserAndDeletedFalse(user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(2)
                .containsExactlyInAnyOrder(transaction, transaction2);
    }

    @Test
    @DisplayName("findByAccountUser should not return transactions from other user")
    void findByAccountUser_shouldNotReturnCategoriesFromOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser(builder -> builder.username("otherUser")));
        createTransaction(otherUser);

        Page<Transaction> transactions = repository.findByAccountUser(user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(3)
                .containsExactlyInAnyOrder(transaction, transaction2, transaction3);
    }

    @Test
    @DisplayName("findByAccountUserAndDeletedFalse should not return transactions from other user")
    void findByAccountUserAndDeletedFalse_shouldNotReturnTransactionsFromOtherUser() {
        User otherUser = entityManager.persist(UserFactory.createUser(builder -> builder.username("otherUser")));
        createTransaction(otherUser);

        Page<Transaction> transactions = repository.findByAccountUserAndDeletedFalse(user, Pageable.unpaged());
        assertThat(transactions)
                .hasSize(2)
                .containsExactlyInAnyOrder(transaction, transaction2);
    }

    private Transaction createTransaction(User user) {
        Account account = entityManager.persist(AccountFactory.createAccount(user));
        Category category = entityManager.persist(CategoryFactory.create(user));
        entityManager.persist(account);
        entityManager.persist(category);
        return entityManager.persist(TransactionFactory.create(account, category));
    }

    private Transaction createDeletedTransaction(User user) {
        Account account = entityManager.persist(AccountFactory.createAccount(user));
        Category category = entityManager.persist(CategoryFactory.create(user));
        entityManager.persist(account);
        entityManager.persist(category);
        return entityManager.persist(TransactionFactory.createDeleted(account, category));
    }
}
