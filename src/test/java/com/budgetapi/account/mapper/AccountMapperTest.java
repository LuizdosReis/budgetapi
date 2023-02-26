package com.budgetapi.account.mapper;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.model.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountMapperTest {

    @Test
    void giveAccountWhenMapsThenCorrect() {
        Account account = Account.builder().id(1L).name("Nubank").currency("BRL").build();

        AccountDTO accountDTO = AccountMapper.MAPPER.toDTO(account);

        assertEquals(account.getCurrency(), accountDTO.currency());
        assertEquals(account.getId(), accountDTO.id());
        assertEquals(account.getName(), accountDTO.name());
    }

    @Test
    void giveNullWhenMapsThenReturnNull() {
        AccountDTO accountDTO = AccountMapper.MAPPER.toDTO(null);

        assertNull(accountDTO);
    }
}
