package com.budgetapi.transaction.mapper;

import com.budgetapi.account.model.Account;
import com.budgetapi.category.model.Category;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.model.Transaction;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface TransactionMapper {

    TransactionMapper MAPPER = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "account", source = "account")
    @Mapping(target = "deleted", ignore = true)
    Transaction toModel(TransactionRequestDTO dto, Account account, Category category, Set<Tag> tags);

    @Mapping(target = "account", source = "account")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "status", source = "dto.status")
    void updateModel(TransactionRequestDTO dto, Account account, Category category, Set<Tag> tags, @MappingTarget Transaction transaction);
}
