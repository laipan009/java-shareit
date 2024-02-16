package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User updateUserFromDto(UserDto userDto, @MappingTarget User existingUser);

    User toUserFromDto(UserDto userDto);
}