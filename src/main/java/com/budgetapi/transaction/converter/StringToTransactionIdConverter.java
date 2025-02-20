package com.budgetapi.transaction.converter;

import com.budgetapi.transaction.model.TransactionId;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

public class StringToTransactionIdConverter implements Converter<String, TransactionId> {

    @Override
    public TransactionId convert(String uuid) {
        return new TransactionId(UUID.fromString(uuid));
    }
}
