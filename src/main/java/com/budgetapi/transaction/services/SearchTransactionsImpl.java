package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class SearchTransactionsImpl implements SearchTransactions {
    private final TransactionRepository repository;
    private final UserService userService;
    private final TransactionMapper mapper;

    @Override
    public Page<TransactionDTO> execute(TransactionSearchCriteria criteria, Pageable pageable) {
        User user = userService.getCurrentUser();
        Page<Transaction> transactions = repository.findAllBy(criteria, user, pageable);
        return transactions.map(mapper::toDTO);
    }
}
