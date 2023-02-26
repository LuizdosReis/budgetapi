package com.budgetapi.account.mapper;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.dto.AccountRequestDTO;
import com.budgetapi.account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper MAPPER = Mappers.getMapper( AccountMapper.class );

    AccountDTO toDTO(Account account);
    Account toModel(AccountRequestDTO dto);
}
