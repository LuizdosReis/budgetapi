package com.budgetapi;

import com.budgetapi.config.RsaKeyProperties;
import com.budgetapi.config.SecurityConfig;
import com.budgetapi.factories.UserFactory;
import com.budgetapi.user.model.User;
import com.budgetapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;

@WithMockUser
@Import({SecurityConfig.class, RsaKeyProperties.class})
public abstract class AbstractControllerTest {

    @MockitoBean
    private UserService userService;

    public final User user = UserFactory.createUser();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(user);
    }

}
