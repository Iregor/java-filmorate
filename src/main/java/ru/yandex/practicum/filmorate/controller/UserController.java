package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final FilmService filmService;

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping(value = "{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void delFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @GetMapping("/{userId}/recommendations")
    public Collection<Film> getRecommendation(@PathVariable Long userId) {
        return filmService.convertIdsToFilms(userService.findAdviseFilmsIds(userId));
    }
}
