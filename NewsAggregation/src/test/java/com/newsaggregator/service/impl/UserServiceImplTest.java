package com.newsaggregator.service.impl;

import com.newsaggregator.dao.IUserDAO;
import com.newsaggregator.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private IUserDAO userDAOMock;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_WhenEmailNotExists_ShouldReturnTrue() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userDAOMock.getUserByEmail("test@example.com")).thenReturn(null);
        when(userDAOMock.createUser(user)).thenReturn(true);

        boolean result = userService.registerUser(user);

        assertTrue(result);
        verify(userDAOMock).getUserByEmail("test@example.com");
        verify(userDAOMock).createUser(user);
    }

    @Test
    void testRegisterUser_WhenEmailExists_ShouldReturnFalse() {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userDAOMock.getUserByEmail("existing@example.com")).thenReturn(existingUser);

        boolean result = userService.registerUser(existingUser);

        assertFalse(result);
        verify(userDAOMock).getUserByEmail("existing@example.com");
        verify(userDAOMock, never()).createUser(any());
    }

    @Test
    void testLoginUser_ShouldReturnUser() {
        String email = "user@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);

        when(userDAOMock.validateLogin(email, password)).thenReturn(user);

        User result = userService.loginUser(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userDAOMock).validateLogin(email, password);
    }

    @Test
    void testGetUser_ShouldReturnUser() {
        String email = "fetch@example.com";
        User user = new User();
        user.setEmail(email);

        when(userDAOMock.getUserByEmail(email)).thenReturn(user);

        User result = userService.getUser(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userDAOMock).getUserByEmail(email);
    }

    @Test
    void testGetSubscribedUsers_ShouldReturnList() {
        User u1 = new User();
        User u2 = new User();
        List<User> mockList = Arrays.asList(u1, u2);

        when(userDAOMock.getSubscribedUsers()).thenReturn(mockList);

        List<User> result = userService.getSubscribedUsers();

        assertEquals(2, result.size());
        verify(userDAOMock).getSubscribedUsers();
    }
}
