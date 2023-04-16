package com.budgetapi.user.service;

import com.budgetapi.user.dto.UserRequestDTO;
import com.budgetapi.user.mapper.UserMapper;
import com.budgetapi.user.model.User;
import com.budgetapi.user.model.UserDetailsImpl;
import com.budgetapi.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found: %s", username)));

        return new UserDetailsImpl(user);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Transactional
    public void save(UserRequestDTO userRequestDTO) {
        User user = mapper.toModel(userRequestDTO);
        userRepository.save(user);
    }
}
