package controller;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    int currentId = 0;
    @PostMapping
    public User add(@RequestBody User user) {
        try {
            validateUserData(user);
        } catch (ValidationException exc){
            log.info(exc.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, "Ошибка валидации пользователя.");
        }
        grantId(user);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: " + user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        try {
            validateUserData(user);
        } catch (ValidationException exc) {
            log.info(exc.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, "Ошибка валидации пользователя.");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь успешно обновлен" + user);
            return user;
        }
        log.info("Пользователя id = " + user.getId() + " нет в базе.");
        throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
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

    private void validateUserData(User user) throws ValidationException{
        if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
            throw new ValidationException("Ошибка валидации пользователя: " + user + ". " + "Ошибки: Не указаны требуемые поля.");
        }

        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        StringBuilder sb = new StringBuilder();
        boolean notValidated = false;

        if (user.getEmail().isBlank()) {
            sb.append("Пустой email. ");
            notValidated = true;
        }
        if (!user.getEmail().contains("@")) {
            sb.append("В email отсутствует символ @. ");
            notValidated = true;
        }
        if (user.getLogin().isBlank()){
            sb.append("Пустой логин. ");
            notValidated = true;
        }
        if (user.getLogin().contains(" ")){
            sb.append("Недопустимый символ пробела. ");
            notValidated = true;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            sb.append("Указана ненаступившая дата рождения. ");
            notValidated = true;
        }
        if (notValidated) {
            throw new ValidationException("Ошибка валидации пользователя: " + user + ". " + "Ошибки: " + sb);
        }
    }
}