package com.budgetapi.transaction.services;

import com.budgetapi.erro.NotFoundException;
import com.budgetapi.transaction.model.Transaction;
import com.budgetapi.transaction.repository.TransactionRepository;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DeleteTransactionImpl implements DeleteTransaction {

    private final TransactionRepository repository;
    private final UserService userService;

    @Override
    public void execute(UUID transactionId) {
        User user = userService.getCurrentUser();
        Transaction transaction = repository.findByIdAndAccountUser(transactionId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction with id %s not found", transactionId)));
        repository.delete(transaction);
    }
}
