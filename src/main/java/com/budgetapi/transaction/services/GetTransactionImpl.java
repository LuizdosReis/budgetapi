package com.budgetapi.transaction.services;

import com.budgetapi.erro.NotFoundException;
import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.mapper.TransactionMapper;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.model.TransactionId;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class GetTransactionImpl implements GetTransaction {
    private final TransactionRepository repository;
    private final UserService userService;
    private final TransactionMapper mapper;

    @Override
    public TransactionDTO execute(TransactionId transactionId) {
        User user = userService.getCurrentUser();
        Transaction transaction = repository.findByIdAndAccountUser(transactionId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction with id %s not found", transactionId.id())));
        return mapper.toDTO(transaction);
    }
}
