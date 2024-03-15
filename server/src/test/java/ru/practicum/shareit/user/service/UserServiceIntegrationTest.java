package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void partialUpdateUserShouldUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");

        UserDto updatedUserDto = userService.partialUpdateUser(user.getId(), userDto);

        assertNotNull(updatedUserDto);
        assertEquals(user.getId(), updatedUserDto.getId());
        assertEquals("Updated Name", updatedUserDto.getName());
        assertEquals(user.getEmail(), updatedUserDto.getEmail());


        User updatedUser = userStorage.findById(user.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }
}