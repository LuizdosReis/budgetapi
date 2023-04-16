package com.budgetapi.account.controller;

import com.budgetapi.account.dto.AccountDTO;
import com.budgetapi.account.dto.AccountRequestDTO;
import com.budgetapi.account.mapper.AccountMapper;
import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.erro.NotFoundException;
import com.budgetapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping(AccountController.BASE_URL)
public class AccountController {

    public static final String BASE_URL = "/accounts";
    private static final String ACCOUNT_NOT_FOUND = "Account with id %s not found";

    private AccountRepository repository;
    private AccountMapper mapper;
    private UserService userService;

    @GetMapping
    public Set<AccountDTO> getAll() {
        return StreamSupport.stream(repository.findAllByUser(userService.getCurrentUser()).spliterator(), false)
                .map(mapper::toDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping(path = "/{id}")
    public AccountDTO getById(@PathVariable UUID id) {
        return repository.findByIdAndUser(id, userService.getCurrentUser())
                .map(mapper::toDTO)
                .orElseThrow(() -> new NotFoundException(String.format(ACCOUNT_NOT_FOUND, id)));
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public AccountDTO create(@RequestBody @Valid AccountRequestDTO accountRequestDTO) {
        Account account = mapper.toModel(accountRequestDTO);
        account.setUser(userService.getCurrentUser());
        repository.save(account);
        return mapper.toDTO(account);
    }

    @PutMapping("/{id}")
    public AccountDTO update(@PathVariable @NotNull UUID id, @RequestBody @Valid AccountRequestDTO accountRequestDTO) {
        return repository.findByIdAndUser(id, userService.getCurrentUser())
                .map(account -> {
                    mapper.updateModel(accountRequestDTO, account);
                    return mapper.toDTO(account);
                })
                .orElseThrow(() -> new NotFoundException(String.format(ACCOUNT_NOT_FOUND, id)));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        Account account = repository.findByIdAndUser(id, userService.getCurrentUser())
                .orElseThrow(() -> new NotFoundException(String.format(ACCOUNT_NOT_FOUND, id)));
        repository.delete(account);
    }
}
