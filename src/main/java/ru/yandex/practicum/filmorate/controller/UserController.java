package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userStorage.findById(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void delFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.delFriend(userId, friendId);
    }
}
