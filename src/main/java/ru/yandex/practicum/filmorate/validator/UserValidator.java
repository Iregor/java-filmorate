package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;


public class UserValidator {
    public static boolean validate(User user, HashMap<Integer, User> users) {
        StringBuilder exceptionMessage = new StringBuilder();
        HashMap<Integer, User> hashUsersLogin = new HashMap<>();
        HashMap<Integer, User> hashUsersEmail = new HashMap<>();
        for (User userFromMap : users.values()) {
            hashUsersLogin.put(userFromMap.getLogin().hashCode(), userFromMap);
            hashUsersEmail.put(userFromMap.getEmail().hashCode(), userFromMap);
        }

        if(user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            exceptionMessage.append("Почта должна быть заполнена и содержать символ \"@\". ");
        } else if(hashUsersEmail.containsKey(user.getEmail().hashCode())) {
            exceptionMessage.append("Пользователь с данной почтой уже зарегистрирован. ");
        }
        if(user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            exceptionMessage.append("Логин не может быть пустым или содержать пробелы. ");
        } else if(hashUsersLogin.containsKey(user.getLogin().hashCode())) {
            exceptionMessage.append("Данный логин занят. ");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            exceptionMessage.append("Дата рождения не может быть в будущем. ");
        }

        if(!exceptionMessage.toString().isBlank()) {
            throw new ValidateException(exceptionMessage.toString());
        }

        //put
        if(user.getId() != null && !users.containsKey(user.getId())) {
            throw new ValidateException("Пользователь с таким идентификатором отсутствует. ");
        }

        return true;
    }
}
