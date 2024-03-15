package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "Test User", "test@example.com");
        user = new User(1, "Test User", "test@example.com");
    }

    @Test
    void testAddUserWhenUserAddedThenReturnUserDto() {
        when(userMapper.toUserFromDto(userDto)).thenReturn(user);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertEquals(userDto, result);
        verify(userStorage).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void testUpdateUserWhenUserUpdatedThenReturnUserDto() {
        when(userMapper.updateUserFromDto(userDto, new User())).thenReturn(user);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userDto);

        assertEquals(userDto, result);
        verify(userStorage).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void testPartialUpdateUserWhenUserUpdatedThenReturnUserDto() {
        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(userStorage.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.updateUserFromDto(userDto, user)).thenReturn(user);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.partialUpdateUser(user.getId(), userDto);

        assertEquals(userDto, result);
        verify(userStorage).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void testPartialUpdateUserWhenEmailIsDuplicatedThenThrowException() {
        user.setEmail("another@example.com");
        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(userStorage.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(EmailDuplicateException.class, () -> userService.partialUpdateUser(user.getId(), userDto));
    }


    @Test
    void testDeleteUserWhenUserDeletedThenUserStorageDeleteByIdCalled() {
        doNothing().when(userStorage).deleteById(user.getId());

        userService.deleteUser(user.getId());

        verify(userStorage).deleteById(user.getId());
    }

    @Test
    void testGetUsersWhenCalledThenUserStorageFindAllCalled() {
        when(userStorage.findAll()).thenReturn(List.of(user));
        when(userMapper.getListUserDto(anyList())).thenReturn(List.of(userDto));

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userDto, result.get(0));
        verify(userStorage).findAll();
    }

    @Test
    void testGetUserByIdWhenCalledThenUserStorageFindByIdCalled() {
        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(user.getId());

        assertEquals(userDto, result);
        verify(userStorage).findById(user.getId());
        verify(userMapper).toUserDto(user);
    }
}