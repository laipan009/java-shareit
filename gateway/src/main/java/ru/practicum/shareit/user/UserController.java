package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserFeignClient feignClient;
    private static final String USER_ID = "userId";

    @Autowired
    public UserController(UserFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return feignClient.getUsers();
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received POST request on GATEWAY on UserController");
        return feignClient.addUser(userDto);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        return feignClient.updateUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto partialUpdateUser(@PathVariable(USER_ID) int userId, @RequestBody UserDto userDto) {
        return feignClient.partialUpdateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(USER_ID) int userId) {
        feignClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(USER_ID) int userId) {
        return feignClient.getUserById(userId);
    }
}