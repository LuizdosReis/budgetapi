package com.budgetapi.auth.controller;

import com.budgetapi.auth.service.TokenService;
import com.budgetapi.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({ AuthController.class })
@Import({ SecurityConfig.class, TokenService.class})
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void shouldGetTokenWhenUsernameAndPasswordAreCorrect() throws Exception{
        this.mvc.perform(post(AuthController.BASE_URL)
                        .with(httpBasic("luiz", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldUnauthorizedWhenUsernameAndPasswordAreIncorrect() throws Exception{
        this.mvc.perform(post(AuthController.BASE_URL)
                        .with(httpBasic("luiz", "incorrect")))
                .andExpect(status().isUnauthorized());
    }

}