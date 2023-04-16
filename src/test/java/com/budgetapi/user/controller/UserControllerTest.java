package com.budgetapi.user.controller;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.mapper.UserMapper;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.budgetapi.user.controller.UserController.BASE_URL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository repository;
    @MockBean
    private UserMapper mapper;

    @Test
    void shouldReturnStatusCreatedWhenCallCreate() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("luizdosreis", "password");
        User user = User.builder().username(userRequestDTO.username()).password(userRequestDTO.password()).build();

        when(mapper.toModel(userRequestDTO)).thenReturn(user);

        this.mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(userRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(repository).save(user);
    }

}