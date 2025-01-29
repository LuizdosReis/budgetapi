package com.budgetapi.user.mapper;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Mock
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