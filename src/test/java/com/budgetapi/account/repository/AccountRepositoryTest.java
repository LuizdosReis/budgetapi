package com.budgetapi.account.repository;

import com.budgetapi.account.model.Account;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private User user;
    private Account account1;
    private Account account2;
    private Account account3;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        entityManager.persist(user);

        account1 = AccountFactory.createAccount(user);
        account2 = AccountFactory.createAccount(user);
        account3 = AccountFactory.createDeletedAccount(user);
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.persist(account3);
        entityManager.flush();
    }

    @Test
    void findAllByUser_shouldReturnAllAccountsForUser() {
        Iterable<Account> accounts = accountRepository.findAllByUser(user);
        assertThat(accounts)
                .hasSize(3)
                .containsExactlyInAnyOrder(account1, account2, account3);
    }

    @Test
    void findAllByUser_shouldReturnAllNotDeletedAccountsForUser() {
        Iterable<Account> accounts = accountRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(accounts)
                .hasSize(2)
                .containsExactlyInAnyOrder(account1, account2);
    }

    @Test
    void findAllByUser_shouldNotReturnAccountsFromOtherUsers() {
        User otherUser = UserFactory.createUser("otherUser");
        entityManager.persist(otherUser);
        Account otherUseraccount = AccountFactory.createAccount(otherUser);
        entityManager.persist(otherUseraccount);
        entityManager.flush();

        Iterable<Account> accounts = accountRepository.findAllByUserAndDeletedIsFalse(user);
        assertThat(accounts)
                .hasSize(2)
                .containsExactlyInAnyOrder(account1, account2)
                .doesNotContain(otherUseraccount);
    }

    @Test
    void findByIdAndUser_shouldReturnAccountWhenExists() {
        Optional<Account> accountOptional = accountRepository.findByIdAndUser(account1.getId(), user);

        assertThat(accountOptional).isPresent().contains(account1);
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenIdDoesNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Account> accountOptional = accountRepository.findByIdAndUser(nonExistentId, user);

        assertThat(accountOptional).isEmpty();
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenAccountBelongsToOtherUser() {
        User otherUser = UserFactory.createUser("otherUser");
        entityManager.persist(otherUser);
        Account otherUseraccount = AccountFactory.createAccount(otherUser);
        entityManager.persist(otherUseraccount);
        entityManager.flush();

        Optional<Account> foundAccount = accountRepository.findByIdAndUser(otherUseraccount.getId(), user);
        assertThat(foundAccount).isEmpty();
    }

    @Test
    void findByIdAndUser_shouldReturnEmptyWhenOtherUserTriesToAccessAccount() {
        User otherUser = UserFactory.createUser("otherUser");
        entityManager.persist(otherUser);

        Optional<Account> foundAccount = accountRepository.findByIdAndUser(account1.getId(), otherUser);
        assertThat(foundAccount).isEmpty();
    }

    @Test
    void delete_shouldSetDeletedToTrue() {
        accountRepository.deleteById(account1.getId());
        entityManager.flush();

        Account account = accountRepository.findById(account1.getId()).orElseThrow();
        assertThat(account.isDeleted()).isTrue();
    }
}
