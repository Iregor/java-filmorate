package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable Long userId) {
        if (userService.findById(userId) == null) {
            throw new IncorrectObjectIdException(String.format("User %d is not found.", userId));
        }
        return userService.findById(userId);
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
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        Map<String, Long> result = userService.addFriend(userId, friendId);
        if(result != null) {
            throw new IncorrectObjectIdException(String.format("Data %s is not found.", result));
        }
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void delFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        Map<String, Long> result = userService.delFriend(userId, friendId);
        if(result != null) {
            throw new IncorrectObjectIdException(String.format("Data %s is not found.", result));
        }
    }
}
