package com.budgetapi.transactions.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.TransactionFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.dto.AccountDTO;
import com.budgetapi.transaction.dto.CategoryDTO;
import com.budgetapi.transaction.dto.TagDTO;
import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.transaction.services.SearchTransactionsImpl;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchTransactionsImplTest {

    @InjectMocks
    private SearchTransactionsImpl searchTransactions;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionRepository repository;

    @Mock
    private UserService userService;

    private User user;
    private Account account;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        account = AccountFactory.createAccount(user);
        category = CategoryFactory.create(user);
        tag = TagFactory.create(user);
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    @DisplayName("execute should return page of DTOs mapped from transactions found by repository")
    void execute_shouldReturnPageOfTransactionDTOsMappedFromRepositoryResults() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().build();
        Transaction transaction = TransactionFactory.create(account, category);
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName(), category.getType().name());
        TagDTO tagDTO = new TagDTO(tag.getId(), tag.getName());
        TransactionDTO dto = new TransactionDTO("description", accountDTO, categoryDTO, Set.of(tagDTO), BigDecimal.TEN, transaction.getId().id(), transaction.getDate(), transaction.getStatus(), transaction.isDeleted());

        when(repository.findAllBy(criteria, user, pageRequest)).thenReturn(new PageImpl<>(List.of(transaction)));
        when(mapper.toDTO(transaction)).thenReturn(dto);

        Page<TransactionDTO> page = searchTransactions.execute(criteria, pageRequest);

        assertThat(page.getContent()).isEqualTo(List.of(dto));
    }

    @Test
    @DisplayName("execute should returns empty page when repository returns empty results")
    void execute_shouldReturnEmptyWhenRepositoryReturnsEmptyResults() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder().build();

        when(repository.findAllBy(criteria, user, pageRequest)).thenReturn(Page.empty());

        Page<TransactionDTO> page = searchTransactions.execute(criteria, pageRequest);

        assertThat(page.getContent()).isEmpty();
    }
}
