package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.CustomFeignException;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.fegin.UserFeign;
import com.example.demo.service.UserService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Test
    void testExceptionThrownWhenEmailNotFoundForLogin() {
        UserController userController = new UserController(new UserService(), null);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("12345");
        assertThrows(EmailNotFoundException.class, () -> userController.login(loginRequest));
    }

    @Test
    void testExceptionThrownWhenFeignConnectionIssueForLogin() throws UsernameNotFoundException {
        UserService userService = mock(UserService.class);
        when(userService.loadUserByUsername((String) any())).thenThrow(mock(FeignException.class));
        UserController userController = new UserController(userService, null);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("12345");
        assertThrows(CustomFeignException.class, () -> userController.login(loginRequest));
        verify(userService).loadUserByUsername((String) any());
    }

    @Test
    void testExceptionThrownWhenFeignConnectionIssueForSignup() {
        UserService userService = mock(UserService.class);
        when(userService.emailIsPresent((String) any())).thenReturn(false);
        UserFeign userFeign = mock(UserFeign.class);
        when(userFeign.createUser((com.example.demo.dto.User) any())).thenThrow(mock(FeignException.class));
        UserController userController = new UserController(userService, userFeign);
        assertThrows(CustomFeignException.class, () -> userController.signup(new com.example.demo.dto.User()));
    }

    @Test
    void testExceptionThrownWhenEmailAlreadyExistForSignup() {
        UserService userService = mock(UserService.class);
        when(userService.emailIsPresent((String) any())).thenReturn(true);
        UserController userController = new UserController(userService, null);
        assertThrows(EmailAlreadyExistException.class, () -> userController.signup(new com.example.demo.dto.User()));
    }
}
