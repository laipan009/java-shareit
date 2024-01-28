package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User updateUserFromDto(User existingUser, UserDto userDto) {
        if (Optional.ofNullable(userDto.getName()).isPresent()) {
            existingUser.setName(userDto.getName());
        }
        if (Optional.ofNullable(userDto.getEmail()).isPresent()) {
            existingUser.setEmail(userDto.getEmail());
        }
        return existingUser;
    }
}