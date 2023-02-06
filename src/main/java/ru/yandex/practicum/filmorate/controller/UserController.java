package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static int userId = 1;
    private HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if(!UserValidator.validate(user)) {
            return null;
        }
        if(users.containsValue(user)) {
            throw new ValidateException("Пользователь уже в базе.");
        }
        if(user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if(!UserValidator.validate(user)) {
            return null;
        }
        if(!users.containsKey(user.getId())) {
            throw new NullPointerException("Пользователь с таким идентификатором отсутствует.");
        }
        users.put(user.getId(), user);
        return user;
    }
}
