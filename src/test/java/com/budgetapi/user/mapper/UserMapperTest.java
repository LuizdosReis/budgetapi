package com.budgetapi.user.mapper;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void giveUserRequestDTOWhenMapsThenCorrect() {
        String password = "password";
        String passwordEncoded = "passwordEncoded";
        UserRequestDTO userRequestDTO = new UserRequestDTO("luizdosreis", password);

        when(passwordEncoder.encode(password)).thenReturn("passwordEncoded");

        User user = userMapper.toModel(userRequestDTO);

        assertEquals(userRequestDTO.username(), user.getUsername());
        assertEquals(passwordEncoded, user.getPassword());
        assertEquals("USER", user.getRoles());
    }

    @Test
    void giveNullToModelWhenMapsThenReturnNull() {
        User user = userMapper.toModel(null);

        assertNull(user);
    }

}