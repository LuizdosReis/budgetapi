package com.budgetapi.auth.controller;

import com.budgetapi.account.controller.AccountController;
import com.budgetapi.auth.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(AuthController.BASE_URL)
public class AuthController {

    public static final String BASE_URL = "/token";
    private final TokenService tokenService;

    @PostMapping()
    public String token(Authentication authentication) {
        log.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        log.debug("Token granted: {}", token);
        return token;
    }
}
