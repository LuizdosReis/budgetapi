package com.budgetapi.transactions.controller;

import com.budgetapi.AbstractControllerTest;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.transaction.controller.TransactionController;
import com.budgetapi.transaction.dto.AccountDTO;
import com.budgetapi.transaction.dto.CategoryDTO;
import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import com.budgetapi.transaction.model.Direction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.model.TransactionStatus;
import com.budgetapi.transaction.services.CreateTransaction;
import com.budgetapi.transaction.services.DeleteTransaction;
import com.budgetapi.transaction.services.GetTransaction;
import com.budgetapi.transaction.services.SearchTransactions;
import com.budgetapi.transaction.services.UpdateTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTransaction createTransaction;

    @MockitoBean
    private UpdateTransaction updateTransaction;

    @MockitoBean
    private DeleteTransaction deleteTransaction;

    @MockitoBean
    private SearchTransactions searchTransactions;

    @MockitoBean
    private GetTransaction getTransaction;

    @Test
    @DisplayName("POST /transactions returns 201 and calls save when payload is valid")
    void post_createsAndReturns201_whenPayloadIsValid() throws Exception {
        TransactionRequestDTO payload = new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);

        this.mockMvc.perform(post(TransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(createTransaction, times(1)).execute(payload);
    }

    @ParameterizedTest
    @DisplayName("POST /transactions return 400 bad request and do not calls save when payload is invalid")
    @MethodSource("invalidTransactionsRequestDTOs")
    void post_doNotSaveAndReturns400_whenPayloadIsInvalid(TransactionRequestDTO payload, String fieldErrorMessage, String field, Object value) throws Exception {
        this.mockMvc.perform(post(TransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Field Validation Errors")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors[*].field", contains(field)))
                .andExpect(jsonPath("$.fieldErrors[*].message", contains(fieldErrorMessage)))
                .andExpect(jsonPath("$.fieldErrors[*].rejectedValue", contains(value)));

        verifyNoInteractions(createTransaction);
    }

    private static Stream<Arguments> invalidTransactionsRequestDTOs() {
        return Stream.of(
                Arguments.of(new TransactionRequestDTO("", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "Description must be between 5 and 50 characters", "description", ""),
                Arguments.of(new TransactionRequestDTO("a", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "Description must be between 5 and 50 characters", "description", "a"),
                Arguments.of(new TransactionRequestDTO("a".repeat(51), UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "Description must be between 5 and 50 characters", "description", "a".repeat(51)),
                Arguments.of(new TransactionRequestDTO(null, UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "Description cannot be null", "description", null),
                Arguments.of(new TransactionRequestDTO("description", null, UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "AccountId cannot be null", "accountId", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), null, Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "CategoryId cannot be null", "categoryId", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "TagIds cannot be null", "tagIds", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), null, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT), "Amount cannot be null", "amount", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, null, TransactionStatus.REGISTERED, Direction.OUT), "Date cannot be null", "date", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), null, Direction.OUT), "Status cannot be null", "status", null),
                Arguments.of(new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, null), "Direction cannot be null", "direction", null)
        );
    }

    @Test
    @DisplayName("POST /transactions do not calls save and returns 400 when payload is empty")
    void post_doNotSaveAndReturns400_whenPayloadIsEmpty() throws Exception {
        this.mockMvc.perform(post(TransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Field Validation Errors")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder("description", "accountId", "categoryId", "tagIds", "amount", "date", "status", "direction")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder("Description cannot be null", "AccountId cannot be null", "CategoryId cannot be null", "TagIds cannot be null", "Amount cannot be null", "Date cannot be null", "Status cannot be null", "Direction cannot be null")))
                .andExpect(jsonPath("$.fieldErrors[*].rejectedValue", contains(null, null, null, null, null, null, null, null)));

        verifyNoInteractions(createTransaction);
    }

    @Test
    @DisplayName("PUT /transactions/{id} returns 200 and calls update")
    void put_updatesAndReturns200_whenPayloadIsValid() throws Exception {
        TransactionId transactionId = new TransactionId();
        TransactionRequestDTO payload = new TransactionRequestDTO("description", UUID.randomUUID(), UUID.randomUUID(), Set.of(UUID.randomUUID()), BigDecimal.TEN, LocalDate.now(), TransactionStatus.REGISTERED, Direction.OUT);

        this.mockMvc.perform(put(TransactionController.BASE_URL + "/" + transactionId.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(updateTransaction, times(1)).execute(transactionId, payload);
    }

    @ParameterizedTest
    @DisplayName("PUT /transactions/{id} returns 400 bad request and does not call update when payload is invalid")
    @MethodSource("invalidTransactionsRequestDTOs")
    void put_doNotSaveAndReturns400_whenPayloadIsInvalid(TransactionRequestDTO payload, String fieldErrorMessage, String field, Object value) throws Exception {
        this.mockMvc.perform(put(TransactionController.BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Field Validation Errors")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors[*].field", contains(field)))
                .andExpect(jsonPath("$.fieldErrors[*].message", contains(fieldErrorMessage)))
                .andExpect(jsonPath("$.fieldErrors[*].rejectedValue", contains(value)));

        verifyNoInteractions(updateTransaction);
    }

    @Test
    @DisplayName("PUT /transactions/{id} do not calls save and returns 400 when payload is empty")
    void put_doNotSaveAndReturns400_whenPayloadIsEmpty() throws Exception {
        this.mockMvc.perform(put(TransactionController.BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Field Validation Errors")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder("description", "accountId", "categoryId", "tagIds", "amount", "date", "status", "direction")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder("Description cannot be null", "AccountId cannot be null", "CategoryId cannot be null", "TagIds cannot be null", "Amount cannot be null", "Date cannot be null", "Status cannot be null", "Direction cannot be null")))
                .andExpect(jsonPath("$.fieldErrors[*].rejectedValue", contains(null, null, null, null, null, null, null, null)));

        verifyNoInteractions(updateTransaction);
    }

    @Test
    @DisplayName("DELETE /transactions/{id} returns 204 and calls delete")
    void delete_returns204() throws Exception {
        TransactionId transactionId = new TransactionId();

        this.mockMvc.perform(delete(TransactionController.BASE_URL + "/" + transactionId.id()))
                .andExpect(status().isNoContent());

        verify(deleteTransaction, times(1)).execute(transactionId);
    }

    @Test
    @DisplayName("DELETE /transaction/{id} returns 404 not found when service throws notFoundException")
    void delete_returns404_whenServiceThrowsNotFoundException() throws Exception {
        TransactionId transactionId = new TransactionId();
        doThrow(new NotFoundException(String.format("Transaction with id %s not found", transactionId.id()))).when(deleteTransaction).execute(transactionId);

        this.mockMvc.perform(delete(TransactionController.BASE_URL + "/" + transactionId.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Transaction with id %s not found", transactionId.id()))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET /transactions returns 200 OK and calls searchTransactions")
    void get_return200AndCallSearchTransaction() throws Exception {
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder()
                .nonDeleted(true)
                .searchTerm("trans")
                .tagIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                .accountIds(Set.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))
                .categoryIds(Set.of(UUID.randomUUID()))
                .sinceDate(LocalDate.now())
                .untilDate(LocalDate.now())
                .build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchTerm", criteria.searchTerm());
        params.add("nonDeleted", String.valueOf(criteria.nonDeleted()));
        criteria.tagIds().forEach(tagId -> params.add("tagIds", tagId.toString()));
        criteria.accountIds().forEach(accountId -> params.add("accountIds", accountId.toString()));
        criteria.categoryIds().forEach(categoryId -> params.add("categoryIds", categoryId.toString()));
        params.add("sinceDate", criteria.sinceDate().toString());
        params.add("untilDate", criteria.untilDate().toString());

        this.mockMvc.perform(get(TransactionController.BASE_URL)
                        .params(params))
                .andExpect(status().isOk());

        verify(searchTransactions, times(1)).execute(criteria, PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("GET /transaction/{id} returns 200 OK with transaction")
    void get_return200And() throws Exception {
        TransactionId transactionId = new TransactionId();
        AccountDTO account = new AccountDTO(UUID.randomUUID(), "account", "BRL");
        CategoryDTO category = new CategoryDTO(UUID.randomUUID(), "category", "type");
        TransactionDTO transaction = new TransactionDTO("description", account, category, Set.of(), BigDecimal.TEN, transactionId.id(), LocalDate.now(), TransactionStatus.REGISTERED, false, Direction.OUT);

        when(getTransaction.execute(transactionId)).thenReturn(transaction);

        this.mockMvc.perform(get(TransactionController.BASE_URL + "/" + transactionId.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.id().toString()))
                .andExpect(jsonPath("$.description").value(transaction.description()))
                .andExpect(jsonPath("$.account.id").value(account.id().toString()))
                .andExpect(jsonPath("$.account.name").value(account.name()))
                .andExpect(jsonPath("$.account.currency").value(account.currency()))
                .andExpect(jsonPath("$.category.id").value(category.id().toString()))
                .andExpect(jsonPath("$.category.name").value(category.name()))
                .andExpect(jsonPath("$.category.type").value(category.type()))
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.amount").value(transaction.amount()))
                .andExpect(jsonPath("$.date").value(transaction.date().toString()))
                .andExpect(jsonPath("$.status").value(transaction.status().name()))
                .andExpect(jsonPath("$.deleted").value(transaction.deleted()))
                .andExpect(jsonPath("$.direction").value(transaction.direction().name()));
    }

    @Test
    @DisplayName("GET /transaction/{id} returns 404 not found when service throws notFoundException")
    void get_returns404_whenServiceThrowsNotFoundException() throws Exception {
        TransactionId transactionId = new TransactionId();
        doThrow(new NotFoundException(String.format("Transaction with id %s not found", transactionId.id()))).when(getTransaction).execute(transactionId);

        this.mockMvc.perform(get(TransactionController.BASE_URL + "/" + transactionId.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Transaction with id %s not found", transactionId.id()))))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
