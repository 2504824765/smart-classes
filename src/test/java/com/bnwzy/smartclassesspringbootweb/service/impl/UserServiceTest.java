package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.IdentityAuthenticationException;
import com.bnwzy.smartclassesspringbootweb.exception.UserAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.WrongPasswordException;
import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserChangePasswordDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserLoginDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserRegisterDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserReturnDTO;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService();
        userService.userRepository = userRepository;
    }

    @Test
    void testUserLoginSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setRole("student");
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("test");
        dto.setPassword("123456");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        assertTrue(userService.userLogin(dto));
    }

    @Test
    void testUserLoginAdminBypassRole() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("adminpass");
        user.setRole("teacher");
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("adminpass");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        assertTrue(userService.userLogin(dto));
    }

    @Test
    void testUserLoginWrongPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setRole("student");
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("test");
        dto.setPassword("wrong");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        assertThrows(WrongPasswordException.class, () -> userService.userLogin(dto));
    }

    @Test
    void testUserLoginWrongRole() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setRole("teacher");
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("test");
        dto.setPassword("123456");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        assertThrows(IdentityAuthenticationException.class, () -> userService.userLogin(dto));
    }

    @Test
    void testUserLoginUserNotFound() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("notfound");
        dto.setPassword("123456");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.userLogin(dto));
    }

    @Test
    void testUserRegisterSuccess() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("pass");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(new User());
        assertTrue(userService.userRegister(dto));
    }

    @Test
    void testUserRegisterAlreadyExist() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("exist");
        dto.setPassword("pass");
        dto.setRole("student");
        Mockito.when(userRepository.findByUsername("exist")).thenReturn(Optional.of(new User()));
        assertThrows(UserAlreadyExistException.class, () -> userService.userRegister(dto));
    }

    @Test
    void testUserChangePasswordSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("old");
        UserChangePasswordDTO dto = new UserChangePasswordDTO();
        dto.setUsername("test");
        dto.setNewPassword("new");
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        assertTrue(userService.userChangePassword(dto));
        assertEquals("new", user.getPassword());
    }

    @Test
    void testUserChangePasswordUserNotFound() {
        UserChangePasswordDTO dto = new UserChangePasswordDTO();
        dto.setUsername("notfound");
        dto.setNewPassword("new");
        Mockito.when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.userChangePassword(dto));
    }

    @Test
    void testGetUserByUsernameSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setRole("student");
        user.setImageURL("img.png");
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        UserReturnDTO dto = userService.getUserByUsername("test");
        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getUsername());
        assertEquals("student", dto.getRole());
        assertEquals("img.png", dto.getImageURL());
    }

    @Test
    void testGetUserByUsernameNotFound() {
        Mockito.when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("notfound"));
    }

    @Test
    void testDeleteUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertTrue(userService.deleteUserById(1L));
    }

    @Test
    void testDeleteUserByIdNotFound() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(2L));
    }

    @Test
    void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setRole("student");
        user.setImageURL("img.png");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserReturnDTO dto = userService.getUserById(1L);
        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getUsername());
        assertEquals("student", dto.getRole());
        assertEquals("img.png", dto.getImageURL());
    }

    @Test
    void testGetUserByIdNotFound() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void testGetAllUser() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("a");
        User user2 = new User();
        user2.setUsername("b");
        users.add(user1);
        users.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUser();
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getUsername());
        assertEquals("b", result.get(1).getUsername());
    }
} 