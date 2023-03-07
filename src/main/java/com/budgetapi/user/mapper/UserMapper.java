package com.budgetapi.user.mapper;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.password()))")
    @Mapping(target = "roles", constant = "USER")
    public abstract User toModel(UserRequestDTO dto);
}
