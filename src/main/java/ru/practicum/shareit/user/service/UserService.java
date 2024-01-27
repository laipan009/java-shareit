package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.List;

@Service
public class UserService {
    private final UserStorageImpl userStorageImpl;

    @Autowired
    public UserService(UserStorageImpl userStorageImpl) {
        this.userStorageImpl = userStorageImpl;
    }

    public User addUser(User user) {
        if (userStorageImpl.isEmailExists(user.getEmail())) {
            throw new EmailDuplicateException("User with same email already exists");
        }
        return userStorageImpl.createUser(user);
    }

    public User updateUser(User user) {
        return userStorageImpl.updateUser(user);
    }

    public User partialUpdateUser(int userId, UserDto userDto) {
        User userById = userStorageImpl.getUserById(userId);
        if (userStorageImpl.isEmailExists(userDto.getEmail()) && !userById.getEmail().equals(userDto.getEmail())) {
            throw new EmailDuplicateException("User with same email already exists");
        }
        User updatedUser = UserMapper.updateUserFromDto(userById, userDto);
        return userStorageImpl.updateUser(updatedUser);
    }

    public int deleteUser(int id) {
        return userStorageImpl.deleteUserById(id);
    }

    public List<User> getUsers() {
        return userStorageImpl.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorageImpl.getUserById(id);
    }

    public Boolean isUserExists(int userId) {
        return userStorageImpl.isUserExists(userId);
    }
}