package com.budgetapi.auth.controller;

import com.budgetapi.account.repository.AccountRepository;
import com.budgetapi.auth.service.TokenService;
import com.budgetapi.config.SecurityConfig;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        Optional<User> userOpt = Optional.of(User.builder()
                .username("luiz")
                .password("$2a$10$smU2UmQgtZ2wObOMhcAr0.MhNN6tWv/Q38JmGev6JmDZZ50a1xfJ2")
                .roles("USER")
                .build());

        when(repository.findByUsername("luiz")).thenReturn(userOpt);
    }

    @Test
    void shouldGetTokenWhenUsernameAndPasswordAreCorrect() throws Exception {
        this.mvc.perform(post(AuthController.BASE_URL)
                        .with(httpBasic("luiz", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldGetUnauthorizedWhenPasswordIsIncorrect() throws Exception{
        this.mvc.perform(post(AuthController.BASE_URL)
                        .with(httpBasic("luiz", "incorrect")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetUnauthorizedWhenUsernameIsIncorrect() throws Exception{
        this.mvc.perform(post(AuthController.BASE_URL)
                        .with(httpBasic("usernameIncorrect", "incorrect")))
                .andExpect(status().isUnauthorized());
    }

}