package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        UserValidator.validate(user);

        for (User userFromBase : users.values()) {
            if(userFromBase.getEmail().equals(user.getEmail())) {
                log.info("The user with this email is already registered.");
                return null;
            }
            if(userFromBase.getLogin().equals(user.getLogin())) {
                log.info("The user with this login is already registered.");
                return null;
            }
        }

        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("The name is automatically assigned from the \"Login\" field. " +
                    "The new name is {}.", user.getLogin());
        }

        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User \"{}\" added. The database contains {} user(s).", user.getLogin(), users.size());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        UserValidator.validate(user);

        if(user.getId() != null && !users.containsKey(user.getId())) {
            log.info("User ID {} missing. ", user.getId());
            throw new NullPointerException("User ID " + user.getId() + " missing.");
        }

        users.put(user.getId(), user);
        log.info("User ID {} updated. ",user.getId());
        return user;
    }
}
