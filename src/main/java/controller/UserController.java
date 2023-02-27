package controller;

import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    int currentId = 0;
    @PostMapping
    public User add(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        grantId(user);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: " + user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (!users.containsKey(user.getId())) {
            log.info("Пользователя id = " + user.getId() + " нет в базе.");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен" + user);
        return user;
    }

    @GetMapping
    public List<User> users(){
        log.info("Список пользователей отправлен.");
        return new ArrayList<>(users.values());
    }

    public int getCurrentId(){
        return currentId;
    }

    private void grantId(User user) {
        user.setId(++currentId);
    }
}