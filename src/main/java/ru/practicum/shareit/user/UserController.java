package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final String USER_ID = "userId";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PatchMapping("/{userId}")
    public User partialUpdateUser(@PathVariable(USER_ID) int userId, @RequestBody UserDto userDto) {
        return userService.partialUpdateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public int deleteUser(@PathVariable(USER_ID) int userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable(USER_ID) int userId) {
        return userService.getUserById(userId);
    }
}