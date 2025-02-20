package com.budgetapi.transactions.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.services.DeleteTransactionImpl;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionsImplTest {

    @InjectMocks
    private DeleteTransactionImpl deleteTransaction;

    @Mock
    private TransactionRepository repository;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    @DisplayName("delete should call repository findByIdAndAccountUser and delete")
    void delete_shouldCallRepositoryFindByIdAndAccountUserAndDelete() {
        Account account = AccountFactory.createAccount(user);
        Category category = CategoryFactory.create(user);
        Transaction transaction = TransactionFactory.create(account, category);
        when(repository.findByIdAndAccountUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));

        deleteTransaction.execute(transaction.getId());

        verify(repository, times(1)).findByIdAndAccountUser(transaction.getId(), user);
        verify(repository, times(1)).delete(transaction);
    }

    @Test
    @DisplayName("delete should throw NotFoundException when tag not exists")
    void delete_shouldThrowNotFoundExceptionWhenTagNotExists() {
        TransactionId id = new TransactionId();
        when(repository.findByIdAndAccountUser(id, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> deleteTransaction.execute(id));

        assertThat(exception.getMessage()).contains(String.format("Transaction with id %s not found", id));
        verify(repository, never()).delete(any(Transaction.class));
    }
}
