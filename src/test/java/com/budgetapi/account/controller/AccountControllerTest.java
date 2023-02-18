package com.budgetapi.account.controller;

import static com.budgetapi.account.controller.AccountController.BASE_URL;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@WebMvcTest(AccountController.class)
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
        when(mapper.accountToAccountDTO(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get(BASE_URL + "/"+ 1))
                .andDo(print())
                .andExpect(status().isOk());
    };
}