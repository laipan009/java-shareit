package ru.practicum.shareit.user.storage;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private Integer idUser = 1;
    private Map<Integer, User> users = new HashMap<>();

    public Boolean isEmailExists(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(generateIdUser());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public int deleteUserById(int id) {
        User removedUser = users.remove(id);
        if (removedUser == null) {
            return 0;
        }
        return 1;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    private Integer generateIdUser() {
        return idUser++;
    }

    public Boolean isUserExists(int userId) {
        return users.get(userId) != null;
    }
}