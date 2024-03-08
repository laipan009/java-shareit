package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserNotExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserDto addUser(UserDto userDto) {
        User userFromDto = userMapper.toUserFromDto(userDto);
        userFromDto = userStorage.save(userFromDto);
        return userMapper.toUserDto(userFromDto);
    }

    public UserDto updateUser(UserDto userDto) {
        return userMapper.toUserDto(userStorage.save(userMapper.updateUserFromDto(userDto, new User())));
    }

    public UserDto partialUpdateUser(int userId, UserDto userDto) {
        User userById = userStorage.findById(userId).orElseThrow();
        if (userStorage.existsByEmail(userDto.getEmail()) && !userById.getEmail().equals(userDto.getEmail())) {
            throw new EmailDuplicateException("User with same email already exists");
        }
        User updatedUser = userMapper.updateUserFromDto(userDto, userById);
        User save = userStorage.save(updatedUser);
        return userMapper.toUserDto(save);
    }

    public void deleteUser(int id) {
        userStorage.deleteById(id);
    }

    public List<User> getUsers() {
        return userStorage.findAll();
    }

    public UserDto getUserById(int id) {
        User user = userStorage.findById(id).orElseThrow(() -> new UserNotExistsException("User not exists"));
        return userMapper.toUserDto(user);
    }
}