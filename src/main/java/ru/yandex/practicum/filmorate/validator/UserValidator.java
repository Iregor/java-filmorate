package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) {
        StringBuilder exceptionMessage = new StringBuilder();

        if(user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            exceptionMessage.append("Почта должна быть заполнена и содержать символ \"@\". ");
        }
        if(user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            exceptionMessage.append("Логин не может быть пустым или содержать пробелы. ");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            exceptionMessage.append("Дата рождения не может быть в будущем. ");
        }

        if(!exceptionMessage.toString().isBlank()) {
            throw new ValidateException(exceptionMessage.toString());
        }
    }
}
