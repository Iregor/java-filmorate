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
        if(UserValidator.validate(user, users)) {
            if(user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("У пользователь \"{}\" отсутствует имя. " +
                        "Имя автоматически назначено от поля \"Login\"." +
                        "Новое имя пользователя - {}.", user.getLogin(), user.getName());
            }
            user.setId(userId++);
            users.put(user.getId(), user);
            log.info("Пользователь \"{}\" добавлен. В базе {} пользовател{}.",user.getLogin(), users.size(), ending());
            return user;
        }
        return null;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if(UserValidator.validate(user, users)) {
            users.put(user.getId(), user);
            log.info("Пользователь c идентификатором \"{}\" обновлен.",user.getId());
            return user;
        }
        return null;
    }

    private String ending() {
        String[] ends = new String[]{"ь", "я", "ей"};
        if ((users.size() > 4) & (users.size() < 21)) return ends[2];
        else if ((users.size() % 10) == 1) return ends[0];
        else if (((users.size() % 10) > 1) & ((users.size() % 10) < 5)) return ends[1];
        else return ends[0];
    }
}
