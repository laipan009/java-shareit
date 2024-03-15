package ru.practicum.shareit.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.FeignConfig;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-server", url = "http://localhost:8090/users", configuration = FeignConfig.class)
public interface UserFeignClient {

    String USER_ID = "userId";

    @GetMapping
    List<UserDto> getUsers();

    @PostMapping
    UserDto addUser(@RequestBody UserDto userDto);

    @PutMapping
    UserDto updateUser(@RequestBody UserDto userDto);

    @PatchMapping("/{userId}")
    UserDto partialUpdateUser(@PathVariable(USER_ID) int userId, @RequestBody UserDto userDto);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable(USER_ID) int userId);

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable(USER_ID) int userId);
}