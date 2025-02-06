package com.budgetapi.transaction.services;

import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.category.model.Category;
import com.budgetapi.category.repository.CategoryRepository;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
import com.budgetapi.transaction.dto.TransactionRequestDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class CreateTransactionImpl implements CreateTransaction {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    @Override
    public void execute(TransactionRequestDTO dto) {
        User user = userService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(dto.accountId(), user)
                .orElseThrow(() -> new NotFoundException(String.format("Account with id %s not found", dto.accountId())));
        Category category = categoryRepository.findByIdAndUser(dto.categoryId(), user)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", dto.categoryId())));
        Set<Tag> tags = tagRepository.findAllByIdInAndUser(dto.tagIds(), user);
        dto.tagIds().forEach(tagId -> {
            if (tags.stream().map(Tag::getId).noneMatch(tagId::equals)) {
                throw new NotFoundException(String.format("Tag with id %s not found", tagId));
            }
        });
        Transaction transaction = mapper.toModel(dto, account, category, tags);
        repository.save(transaction);
    }
}
