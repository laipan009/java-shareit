package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorageImpl userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (userStorage.isEmailExists(user.getEmail())) {
            throw new EmailDuplicateException("User with same email already exists");
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User partialUpdateUser(int userId, UserDto userDto) {
        User userById = userStorage.getUserById(userId);
        if (userStorage.isEmailExists(userDto.getEmail()) && !userById.getEmail().equals(userDto.getEmail())) {
            throw new EmailDuplicateException("User with same email already exists");
        }
        User updatedUser = UserMapper.updateUserFromDto(userById, userDto);
        return userStorage.updateUser(updatedUser);
    }

    public int deleteUser(int id) {
        return userStorage.deleteUserById(id);
    }

    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public Boolean isUserExists(int userId) {
        return userStorage.isUserExists(userId);
    }
}