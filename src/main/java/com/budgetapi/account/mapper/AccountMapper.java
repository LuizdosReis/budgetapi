package com.budgetapi.account.mapper;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDTO accountToAccountDTO(Account account) {
        return new AccountDTO(account.getId(), account.getName(), account.getCurrency());
    }
}
