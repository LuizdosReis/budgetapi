package com.budgetapi.user.controller;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.service.UserService;
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
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        userService.save(userRequestDTO);
    }

}
