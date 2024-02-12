package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User updateUserFromDto(User existingUser, UserDto userDto) {
        Optional.ofNullable(userDto.getName()).ifPresent(existingUser::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(existingUser::setEmail);
        return existingUser;
    }

    public static User toUserFromDto(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

    }
}