package com.budgetapi.transactions.controller;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.factories.AccountFactory;
import com.budgetapi.factories.CategoryFactory;
import com.budgetapi.factories.TagFactory;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.controller.TransactionController;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class TransactionControllerITest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EntityManager entityManager;

    private UUID accountId;
    private UUID categoryId;
    private UUID tagId;

    @BeforeEach
    void setUp() {
        User user = UserFactory.createUser(c -> c.username("user"));
        entityManager.persist(user);
        Account account = AccountFactory.createAccount(user);
        entityManager.persist(account);
        accountId = account.getId();
        Category category = CategoryFactory.create(user);
        entityManager.persist(category);
        categoryId = category.getId();
        Tag tag = TagFactory.create(user);
        entityManager.persist(tag);
        tagId = tag.getId();
    }

    @Test
    @DisplayName("POST /transactions returns 201")
    void post_createsAndReturns201_whenPayloadIsValid() throws Exception {
        TransactionRequestDTO payload = new TransactionRequestDTO("description", accountId, categoryId, Set.of(tagId), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED);

        this.mockMvc.perform(post(TransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }
}
