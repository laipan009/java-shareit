package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;


public interface UserStorage extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);

    Boolean existsUserById(Integer userId);
}