package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserTest(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);

        assertEquals(0, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    @Test
    public void createUserPasswordMismatchTest(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("errorPassword");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserPasswordtooShortTest(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("1234");
        request.setConfirmPassword("1234");

        ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void test_findById(){
        long id = 1L;
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();

        assertNotNull(actualUser);

        assertEquals(id, actualUser.getId());
        assertEquals("testUser", actualUser.getUsername());
        assertEquals("testPassword", actualUser.getPassword());
    }

    @Test
    public void test_findByUserName(){
        long id = 1L;
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setId(id);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("testUser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();

        assertNotNull(actualUser);

        assertEquals(id, actualUser.getId());
        assertEquals("testUser", actualUser.getUsername());
        assertEquals("testPassword", actualUser.getPassword());
    }

}
