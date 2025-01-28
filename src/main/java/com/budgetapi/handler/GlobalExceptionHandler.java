package com.budgetapi.handler;

import com.budgetapi.erro.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage(), ex);

        BindingResult result = ex.getBindingResult();
        Set<FieldError> fieldErrors = result.getFieldErrors()
                .stream()
                .map(fieldError -> new FieldError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
                .collect(Collectors.toSet());

        Set<GlobalError> globalErrors = result.getGlobalErrors()
                .stream()
                .map(objectError -> new GlobalError(objectError.getCode()))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new ValidationErrorDetails(LocalDateTime.now(), "Field Validation Errors", fieldErrors, globalErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<Object> handleResourceNotFoundException(NotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorDetails(LocalDateTime.now(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
