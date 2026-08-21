package com.budgetapi.transactions.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.transaction.dto.AccountDTO;
import com.budgetapi.transaction.dto.CategoryDTO;
import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.services.GetTransactionImpl;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionImplTest {

    @InjectMocks
    private GetTransactionImpl getTransaction;

    @Mock
    private TransactionMapper mapper;

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
    @DisplayName("Execute should call repository find by id and account user")
    void execute_shouldCallRepositoryFindByIdAndAccountUser() {
        Category category = CategoryFactory.create(user);
        Account account = AccountFactory.createAccount(user);
        Transaction transaction = TransactionFactory.create(account, category);

        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName(), category.getType().name());
        TransactionDTO dto = new TransactionDTO("description", accountDTO, categoryDTO, Set.of(), BigDecimal.TEN, transaction.getId().id(), transaction.getDate(), transaction.getStatus(), transaction.isDeleted(), Direction.OUT);
        when(repository.findByIdAndAccountUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));
        when(mapper.toDTO(transaction)).thenReturn(dto);

        TransactionDTO transactionDTO = getTransaction.execute(transaction.getId());

        verify(mapper, times(1)).toDTO(transaction);
        verify(repository, times(1)).findByIdAndAccountUser(transaction.getId(), user);
        assertThat(transactionDTO).isEqualTo(dto);
    }

    @Test
    @DisplayName("Execute should throw NotFoundException when transaction not exists")
    void execute_shouldThrowNotFoundExceptionWhenTransactionNotExists() {
        TransactionId transactionId = new TransactionId();
        when(repository.findByIdAndAccountUser(transactionId, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> getTransaction.execute(transactionId));

        assertThat(exception.getMessage()).contains(String.format("Transaction with id %s not found", transactionId.id()));
    }
}
