package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private HashMap<Integer, User> users;

    @BeforeEach
    void beforeEach() {
        users = new HashMap<>();
    }

    @Test
    void validateUser_returnException_emailWithoutAt() {
        User user = new User("Emailemail.ru", "User1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user, users)
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". ");
    }

    @Test
    void validateUser_returnException_nullEmail() {
        User user = new User(null, "User1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user, users)
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". ");
    }

    @Test
    void validateUser_returnException_nullLogin() {
        User user = new User("Email@email.ru", null, "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user, users)
        );
        assertEquals(exception.getMessage(),
                "Логин не может быть пустым или содержать пробелы. ");
    }

    @Test
    void validateUser_returnException_loginWithSpace() {
        User user = new User("Email@email.ru", "Login 1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user, users)
        );
        assertEquals(exception.getMessage(),
                "Логин не может быть пустым или содержать пробелы. ");
    }

    @Test
    void validateUser_returnException_withFutureBirthday() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "2994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user, users)
        );
        assertEquals(exception.getMessage(),
                "Дата рождения не может быть в будущем. ");
    }

    @Test
    void validateUser_returnException_userEmailInBase() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "1994-11-25");
        if(UserValidator.validate(user, users)) {
            user.setId(13);
            users.put(user.getId(), user);
        }

        User newUser = new User("Email@email.ru", "Log123in", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    if(UserValidator.validate(newUser, users)) {
                        newUser.setId(10);
                        users.put(newUser.getId(), newUser);
                    }
                }
        );
        assertEquals(exception.getMessage(),
                "Пользователь с данной почтой уже зарегистрирован. ");
    }

    @Test
    void validateUser_returnException_userLoginInBase() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "1994-11-25");
        if(UserValidator.validate(user, users)) {
            user.setId(13);
            users.put(user.getId(), user);
        }

        User newUser = new User("Email@em123ail.ru", "Login", "Юз123ер 1", "1981-12-13");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    if(UserValidator.validate(newUser, users)) {
                        newUser.setId(10);
                        users.put(newUser.getId(), newUser);
                    }
                }
        );
        assertEquals(exception.getMessage(),
                "Данный логин занят. ");
    }

    @Test
    void validateUser_returnException_wrongUserId() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "1994-11-25");
        if(UserValidator.validate(user, users)) {
            user.setId(13);
            users.put(user.getId(), user);
        }

        User newUser = new User("E123l@email.ru", "L123ogin", "Юз123ер 1", "1981-12-13");
        newUser.setId(234);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    if(UserValidator.validate(newUser, users)) {
                        users.put(newUser.getId(), newUser);
                    }
                }
        );
        assertEquals(exception.getMessage(),
                "Пользователь с таким идентификатором отсутствует. ");
    }

    @Test
    void validateUser_returnManyException_wrongUser() {
        User user = new User(null, null, "Юзер 1", "2994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    if(UserValidator.validate(user, users)) {
                        user.setId(12);
                        users.put(user.getId(), user);
                    }
                }
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". " +
                        "Логин не может быть пустым или содержать пробелы. " +
                        "Дата рождения не может быть в будущем. ");
    }

    @Test
    void validateUser_returnManyException_userInBase() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "1994-11-25");
        if(UserValidator.validate(user, users)) {
            user.setId(12);
            users.put(user.getId(), user);
        }

        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    if(UserValidator.validate(user, users)) {
                        user.setId(12);
                        users.put(user.getId(), user);
                    }
                }
        );
        assertEquals(exception.getMessage(),
                "Пользователь с данной почтой уже зарегистрирован. " +
                        "Данный логин занят. ");
    }

}