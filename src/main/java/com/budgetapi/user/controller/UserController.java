package com.budgetapi.user.controller;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.mapper.UserMapper;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL = "/users";
    private final UserRepository repository;
    private final UserMapper mapper;

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        User user = mapper.toModel(userRequestDTO);
        repository.save(user);
    }

}
