package com.budgetapi.account.controller;

import com.budgetapi.AbstractControllerTest;
import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.dto.AccountRequestDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.budgetapi.account.controller.AccountController.BASE_URL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountRepository repository;
    @MockBean
    private AccountMapper mapper;

    @Test
    void shouldReturnStatusOkWhenCallGetById() throws Exception {
        UUID uuid = UUID.randomUUID();
        Account account = Account.builder().id(uuid).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(repository.findByIdAndUser(account.getId(), user)).thenReturn(Optional.of(account));
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL + "/" + uuid))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAccountWithIdNotExists() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(repository.findById(uuid)).thenReturn(Optional.empty());

        this.mockMvc.perform(get(BASE_URL + "/" + uuid))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotIncludeDeletedAccountWhenCallGetAllWithoutIncludeDeletedParam() throws Exception {
        UUID uuid = UUID.randomUUID();
        Account account = Account.builder().id(uuid).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(repository.findAllByUserAndDeletedIsFalse(user)).thenReturn(List.of(account));
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());

        verify(repository, times(1)).findAllByUserAndDeletedIsFalse(user);
    }

    @Test
    void shouldIncludeAllAccountsWhenCallGetWithIncludeDeletedParam() throws Exception {
        UUID uuid = UUID.randomUUID();
        Account account = Account.builder().id(uuid).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(repository.findAllByUser(user)).thenReturn(List.of(account));
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL).queryParam("includeDeleted", "true"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(repository, times(1)).findAllByUser(user);
    }

    @Test
    void shouldReturnEmptyListWhenFindAllReturnEmpty() throws Exception {
        when(repository.findAllByUserAndDeletedIsFalse(user)).thenReturn(List.of());

        this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnStatusCreatedWhenCallCreate() throws Exception {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Nubank", "BRL");
        Account account = Account.builder().id(UUID.randomUUID()).name(accountRequestDTO.name()).currency(accountRequestDTO.currency()).build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(mapper.toModel(accountRequestDTO)).thenReturn(account);
        when(repository.save(account)).thenReturn(account);
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(account.getName())))
                .andExpect(jsonPath("$.currency", is(account.getCurrency())))
                .andExpect(jsonPath("$.id", is(account.getId().toString())));
    }

    @ParameterizedTest
    @MethodSource("invalidAccounts")
    void shouldReturnBadRequestWhenCallCreateWithInvalidAccount(AccountRequestDTO accountRequestDTO) throws Exception {
        this.mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private static Stream<AccountRequestDTO> invalidAccounts() {
        return Stream.of(
                new AccountRequestDTO("", "BRL"),
                new AccountRequestDTO(null, "BRL"),
                new AccountRequestDTO("a", "BRL"),
                new AccountRequestDTO("a".repeat(51), "BRL"),
                new AccountRequestDTO("nubank", ""),
                new AccountRequestDTO("nubank", null),
                new AccountRequestDTO("nubank", "a"),
                new AccountRequestDTO("nubank", "a".repeat(4))
        );
    }

    @Test
    void shouldUpdateAccountWhenCallEditEndpoint() throws Exception {
        UUID accountId = UUID.randomUUID();
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Santander", "EUR");
        Account account = Account.builder().id(accountId).name("Nubank").user(user).currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), accountRequestDTO.name(), accountRequestDTO.currency());

        when(repository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(account);
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(put(BASE_URL + "/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(accountRequestDTO.name())))
                .andExpect(jsonPath("$.currency", is(accountRequestDTO.currency())))
                .andExpect(jsonPath("$.id", is(accountId.toString())));

        verify(repository).save(account);
    }

    @Test
    void shouldReturnNotFoundWhenUpdateAccountWithIdNotExists() throws Exception {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Santander", "EUR");

        UUID accountId = UUID.randomUUID();
        when(repository.findById(accountId)).thenReturn(Optional.empty());

        this.mockMvc.perform(put(BASE_URL + "/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidAccounts")
    void shouldReturnBadRequestWhenCallUpdateWithInvalidAccount(AccountRequestDTO accountRequestDTO) throws Exception {

        this.mockMvc.perform(put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotContentWhenDeleteAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).name("Nubank").currency("BRL").build();

        when(repository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));

        this.mockMvc.perform(delete(BASE_URL + "/" + accountId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteAccountWithNoExistId() throws Exception {
        UUID accountId = UUID.randomUUID();

        when(repository.findById(accountId)).thenReturn(Optional.empty());

        this.mockMvc.perform(delete(BASE_URL + "/" + accountId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}