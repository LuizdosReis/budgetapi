package com.budgetapi.transactions.controller;

import com.budgetapi.ClearDatabase;
import com.budgetapi.EnableTestcontainers;
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
import com.budgetapi.transaction.controller.TransactionController;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ClearDatabase
@EnableTestcontainers
class TransactionControllerITest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private Account account;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(UserFactory.createUser());
        account = accountRepository.save(AccountFactory.createAccount(user));
        category = categoryRepository.save(CategoryFactory.create(user));
        tag = tagRepository.save(TagFactory.create(user));
    }

    @Test
    @DisplayName("POST /transactions returns 201")
    void post_createsAndReturns201_whenPayloadIsValid() throws Exception {
        TransactionRequestDTO payload = new TransactionRequestDTO("description", account.getId(), category.getId(), Set.of(tag.getId()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        this.mockMvc.perform(post(TransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        Set<Transaction> transactions = transactionRepository.findAllFetchTags();
        assertThat(transactions).hasSize(1);
        Transaction transaction = transactions.iterator().next();
        assertThat(transaction.getDescription()).isEqualTo(payload.description());
        assertThat(transaction.getAccount().getId()).isEqualTo(payload.accountId());
        assertThat(transaction.getCategory().getId()).isEqualTo(payload.categoryId());
        assertThat(transaction.getTags()).hasSize(1);
        assertThat(transaction.getTags().iterator().next().getId()).isEqualTo(tag.getId());
        assertThat(transaction.getAmount()).isEqualByComparingTo(payload.amount());
        assertThat(transaction.getDate()).isEqualTo(payload.date());
        assertThat(transaction.getStatus()).isEqualTo(payload.status());
        assertThat(transaction.getCreateDate()).isNotNull();
        assertThat(transaction.getModifiedDate()).isNull();
    }

    @Test
    @DisplayName("PUT /transactions/{id} returns 200")
    void put_createsAndReturns200_whenPayloadIsValid() throws Exception {
        Transaction transaction = transactionRepository.save(TransactionFactory.create(account, category));
        TransactionRequestDTO payload = new TransactionRequestDTO("updateddescription", account.getId(), category.getId(), Set.of(tag.getId()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        this.mockMvc.perform(put(TransactionController.BASE_URL + "/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        Optional<Transaction> transactionOptional = transactionRepository.findByIdFetchTags(transaction.getId());
        assertThat(transactionOptional).isPresent();
        Transaction updatedTransaction = transactionOptional.get();
        assertThat(updatedTransaction.getDescription()).isEqualTo(payload.description());
        assertThat(updatedTransaction.getAccount().getId()).isEqualTo(payload.accountId());
        assertThat(updatedTransaction.getCategory().getId()).isEqualTo(payload.categoryId());
        assertThat(updatedTransaction.getTags()).hasSize(1);
        assertThat(updatedTransaction.getTags().iterator().next().getId()).isEqualTo(tag.getId());
        assertThat(updatedTransaction.getAmount()).isEqualByComparingTo(payload.amount());
        assertThat(updatedTransaction.getDate()).isEqualTo(payload.date());
        assertThat(updatedTransaction.getStatus()).isEqualTo(payload.status());
        assertThat(updatedTransaction.getCreateDate()).isCloseTo(transaction.getCreateDate(), byLessThan(1, ChronoUnit.MICROS));
        assertThat(updatedTransaction.getModifiedDate()).isNotNull();
    }
}
