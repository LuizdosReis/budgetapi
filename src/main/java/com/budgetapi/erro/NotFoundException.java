package com.budgetapi.erro;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1302788753269192253L;

    public NotFoundException(String message) {
        super(message);
    }
}
