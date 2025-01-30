package com.budgetapi.config;

import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomUsernamePasswordAuthenticationFilterTest {

    private static final String URL = "/login";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    MockMvc mvc;

    @MockitoBean
    private UserRepository userRepository;

    private final User user = UserFactory.createUser();

    @BeforeEach
    void setUp() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void shouldGetTokenWhenUsernameAndPasswordAreCorrect() throws Exception {
        this.mvc.perform(post(URL)
                        .param(USERNAME, user.getUsername())
                        .param(PASSWORD, "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldGetUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        this.mvc.perform(post(URL)
                        .param(USERNAME, "luiz")
                        .param(PASSWORD, "incorrectPassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetUnauthorizedWhenUsernameIsIncorrect() throws Exception {
        this.mvc.perform(post(URL)
                        .param(USERNAME, "incorrectUsername")
                        .param(PASSWORD, "password"))
                .andExpect(status().isUnauthorized());
    }
}
