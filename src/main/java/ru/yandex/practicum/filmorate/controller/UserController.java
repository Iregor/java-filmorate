package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Exist;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable @Exist("user") Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid @Exist("user") User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping
    public User deleteUserById(@Exist("user") Integer id) {
        return userService.deleteUserById(id);
    }

    @GetMapping
    public Collection<User> users(){
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Set<Integer> addFriend(@PathVariable @Exist("user") Integer id, @PathVariable @Exist("user") Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Set<Integer> deleteFriend(@PathVariable @Exist("user") Integer id, @Exist("user") @PathVariable Integer friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findPersonalFriendSet(@PathVariable @Exist("user") Integer id) {
        return userService.findPersonalFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends (@PathVariable @Exist("user") Integer id, @PathVariable @Exist("user") Integer otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}