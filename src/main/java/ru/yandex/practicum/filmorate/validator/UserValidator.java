package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


public class UserValidator {
    public static boolean validate(User user) {
        if(user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            throw new ValidateException("Почта должна быть заполнена и содержать символ \"@\".");
        }
        if(user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains("\\s")) {
            throw new ValidateException("Логин не может быть пустым или содержать пробелы.");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("Дата рождения не может быть в будущем.");
        }
        return true;
    }
}
