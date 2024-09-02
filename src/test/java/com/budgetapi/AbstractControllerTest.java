package com.budgetapi;

import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public abstract class AbstractControllerTest {

    @MockBean
    private UserRepository userRepository;

    public final User user = UserFactory.createUser();

    @BeforeEach
    void setUp() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

}
