package com.budgetapi.account.mapper;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.dto.AccountRequestDTO;
import com.budgetapi.account.model.Account;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountMapperTest {

    @Test
    void giveAccountWhenMapsThenCorrect() {
        Account account = Account.builder().id(UUID.randomUUID()).name("Nubank").currency("BRL").build();

        AccountDTO accountDTO = AccountMapper.MAPPER.toDTO(account);

        assertEquals(account.getCurrency(), accountDTO.currency());
        assertEquals(account.getId(), accountDTO.id());
        assertEquals(account.getName(), accountDTO.name());
    }

    @Test
    void giveNullToDToWhenMapsThenReturnNull() {
        AccountDTO accountDTO = AccountMapper.MAPPER.toDTO(null);

        assertNull(accountDTO);
    }

    @Test
    void giveAccountRequestDTOWhenMapsThenCorrect() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Nubank", "BRL");

        Account account = AccountMapper.MAPPER.toModel(accountRequestDTO);

        assertEquals(accountRequestDTO.currency(), account.getCurrency());
        assertEquals(accountRequestDTO.name(), account.getName());
    }

    @Test
    void giveNullToModelWhenMapsThenReturnNull() {
        Account account = AccountMapper.MAPPER.toModel(null);

        assertNull(account);
    }

    @Test
    void giveAccountRequestDTOWhenUpdateThenCorrect() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("Santander", "EUR");
        Account account = Account.builder().id(UUID.randomUUID()).name("Nubank").currency("BRL").build();

        AccountMapper.MAPPER.updateModel(accountRequestDTO, account);

        assertEquals(accountRequestDTO.currency(), account.getCurrency());
        assertEquals(accountRequestDTO.name(), account.getName());
    }

    @Test
    void giveNullAccountRequestDTOWhenUpdateThenCorrect() {
        Account account = Account.builder().id(UUID.randomUUID()).name("Nubank").currency("BRL").build();

        AccountMapper.MAPPER.updateModel(null, account);

        assertEquals(account.getCurrency(), account.getCurrency());
        assertEquals(account.getName(), account.getName());
    }
}
