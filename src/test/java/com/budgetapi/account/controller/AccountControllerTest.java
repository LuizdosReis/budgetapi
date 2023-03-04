package com.budgetapi.account.controller;

import static com.budgetapi.account.controller.AccountController.BASE_URL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.dto.AccountRequestDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WithMockUser
@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountRepository repository;
    @MockBean
    private AccountMapper mapper;

    @Test
    void shouldReturnStatusOkWhenCallGetById() throws Exception {
        Account account = Account.builder().id(1L).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL + "/"+ 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAccountWithIdNotExists() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(get(BASE_URL + "/"+ 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStatusOkWhenCallGetAll() throws Exception {
        Account account = Account.builder().id(1L).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(repository.findAll()).thenReturn(List.of(account));
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyListWhenFindAllReturnEmpty() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(0)));
    }

    @Test
    void shouldReturnStatusCreatedWhenCallCreate() throws Exception {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Nubank", "BRL");
        Account account = Account.builder().id(1L).name(accountRequestDTO.name()).currency(accountRequestDTO.currency()).build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), account.getName(), account.getCurrency());

        when(mapper.toModel(accountRequestDTO)).thenReturn(account);
        when(repository.save(account)).thenReturn(account);
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",  is(account.getName())))
                .andExpect(jsonPath("$.currency",  is(account.getCurrency())))
                .andExpect(jsonPath("$.id",  is(account.getId()),  Long.class));
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
    void shouldUpdateAccountWhenCallCreate() throws Exception {
        Long accountId = 1L;
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Santander", "EUR");
        Account account = Account.builder().id(accountId).name("Nubank").currency("BRL").build();
        AccountDTO accountDTO = new AccountDTO(account.getId(), accountRequestDTO.name(), accountRequestDTO.currency());

        when(repository.findById(accountId)).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(account);
        when(mapper.toDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(put(BASE_URL + "/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(accountRequestDTO.name())))
                .andExpect(jsonPath("$.currency", is(accountRequestDTO.currency())))
                .andExpect(jsonPath("$.id", is(accountId), Long.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateAccountWithIdNotExists() throws Exception {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Santander", "EUR");

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        this.mockMvc.perform(put(BASE_URL + "/"+ 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidAccounts")
    void shouldReturnBadRequestWhenCallUpdateWithInvalidAccount(AccountRequestDTO accountRequestDTO) throws Exception {
        this.mockMvc.perform(put(BASE_URL + "/"+ 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(accountRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotContentWhenDeleteAccount() throws Exception {
        Long accountId = 1L;
        Account account = Account.builder().id(accountId).name("Nubank").currency("BRL").build();

        when(repository.findById(accountId)).thenReturn(Optional.of(account));

        this.mockMvc.perform(delete(BASE_URL + "/"+ 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteAccountWithNoExistId() throws Exception {
        Long accountId = 1L;

        when(repository.findById(accountId)).thenReturn(Optional.empty());

        this.mockMvc.perform(delete(BASE_URL + "/" + accountId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}