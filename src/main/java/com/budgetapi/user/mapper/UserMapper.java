package com.budgetapi.user.mapper;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toModel(UserRequestDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .roles("USER")
                .build();
    }
}
