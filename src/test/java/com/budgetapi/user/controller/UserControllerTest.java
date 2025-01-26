package com.budgetapi.user.controller;

import com.budgetapi.AbstractControllerTest;
import com.budgetapi.user.dto.UserRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.budgetapi.user.controller.UserController.BASE_URL;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnStatusCreatedWhenCallCreate() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("luizdosreis", "password");

        this.mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(userRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(userService).save(userRequestDTO);
    }

}