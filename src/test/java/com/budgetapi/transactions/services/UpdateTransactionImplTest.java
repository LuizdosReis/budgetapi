package com.budgetapi.transactions.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.services.UpdateTransactionImpl;
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
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionImplTest {

    @InjectMocks
    private UpdateTransactionImpl updateTransaction;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID tagId = UUID.randomUUID();
    private final Set<UUID> tagIds = Set.of(tagId);
    private User user;
    private Account account;
    private Category category;
    private Set<Tag> tags;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        account = AccountFactory.createAccount(user);
        category = CategoryFactory.create(user);
        tags = Set.of(TagFactory.create(user, c -> c.id(tagId)));
        when(userService.getCurrentUser()).thenReturn(user);
    }


    @Test
    @DisplayName("execute should call mapper and repository")
    void execute_shouldCallMapperAndRepository() {
        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, tagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);
        Transaction transaction = TransactionFactory.create(account, category);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(tagRepository.findAllByIdInAndUser(tagIds, user)).thenReturn(tags);
        when(repository.findByIdAndAccountUser(transaction.getId(), user)).thenReturn(Optional.of(transaction));

        updateTransaction.execute(transaction.getId(), dto);

        verify(mapper, times(1)).updateModel(dto, account, category, tags, transaction);
        verify(repository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("execute should throw NotFoundException when account is not found ")
    void execute_shouldThrowNotFoundException_whenAccountIsNotFound() {
        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, tagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        TransactionId transactionId = new TransactionId();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> updateTransaction.execute(transactionId, dto));
        assertThat(exception.getMessage()).contains(String.format("Account with id %s not found", accountId));
        verifyNoInteractions(repository);

    }

    @Test
    @DisplayName("execute should throw NotFoundException when category is not found ")
    void execute_shouldThrowNotFoundException_whenCategoryIsNotFound() {
        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, tagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        TransactionId transactionId = new TransactionId();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> updateTransaction.execute(transactionId, dto));
        assertThat(exception.getMessage()).contains(String.format("Category with id %s not found", categoryId));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("execute should throw NotFoundException when tag is not found")
    void execute_shouldThrowNotFoundException_whenTagIsNotFound() {
        UUID notFoundTagId = UUID.randomUUID();
        Set<UUID> notFoundTagIds = Set.of(notFoundTagId, tagId);

        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, notFoundTagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(tagRepository.findAllByIdInAndUser(notFoundTagIds, user)).thenReturn(tags);

        TransactionId transactionId = new TransactionId();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> updateTransaction.execute(transactionId, dto));
        assertThat(exception.getMessage()).contains(String.format("Tag with id %s not found", notFoundTagId));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("execute should throw NotFoundException when transaction is not found ")
    void execute_shouldThrowNotFoundException_whenTransactionIsNotFound() {
        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, tagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);
        TransactionId transactionId = new TransactionId();

        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(tagRepository.findAllByIdInAndUser(tagIds, user)).thenReturn(tags);
        when(repository.findByIdAndAccountUser(transactionId, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> updateTransaction.execute(transactionId, dto));
        assertThat(exception.getMessage()).contains(String.format("Transaction with id %s not found", transactionId));
        verify(repository, never()).save(any());
    }
}
