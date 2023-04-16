package com.budgetapi.config;

import com.budgetapi.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomUsernamePasswordAuthenticationFilterTest extends AbstractControllerTest {

    private static final String URL = "/login";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    @Autowired
    MockMvc mvc;

    @Test
    void shouldGetTokenWhenUsernameAndPasswordAreCorrect() throws Exception {
        this.mvc.perform(post(URL)
                        .param(USERNAME, "user")
                        .param(PASSWORD, "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
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
