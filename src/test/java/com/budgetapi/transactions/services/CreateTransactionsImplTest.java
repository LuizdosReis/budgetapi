package com.budgetapi.transactions.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.services.CreateTransactionImpl;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionsImplTest {

    @InjectMocks
    private CreateTransactionImpl createTransaction;

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


    @Test
    @DisplayName("execute should call mapper and repository")
    void execute_shouldCallMapperAndRepository() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Set<UUID> tagIds = Set.of(UUID.randomUUID());

        User user = UserFactory.createUser();
        Account account = AccountFactory.createAccount(user);
        Category category = CategoryFactory.create(user);
        Set<Tag> tags = Set.of(TagFactory.create(user));
        TransactionRequestDTO dto = new TransactionRequestDTO("description", accountId, categoryId, tagIds, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        Transaction transaction = TransactionFactory.create(account, category);

        when(userService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(tagRepository.findAllByIdInAndUser(tagIds, user)).thenReturn(tags);
        when(mapper.toModel(dto, account, category, tags)).thenReturn(transaction);

        createTransaction.execute(dto);

        verify(repository, times(1)).save(transaction);

    }
}
