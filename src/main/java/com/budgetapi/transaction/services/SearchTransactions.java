package com.budgetapi.transaction.services;

import com.budgetapi.transaction.dto.TransactionDTO;
import com.budgetapi.transaction.dto.TransactionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTransactions {
    Page<TransactionDTO> execute(TransactionSearchCriteria criteria, Pageable pageable);
}
