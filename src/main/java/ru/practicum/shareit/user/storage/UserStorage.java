package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User createUser(User user);

    int deleteUserById(int id);

    User updateUser(User user);

    User getUserById(int id);
}
