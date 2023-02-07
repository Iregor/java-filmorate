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
    void validateUser_throwValidateException_emailWithoutAt() {
        User user = new User("Emailemail.ru", "User1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user)
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". ");
    }

    @Test
    void validateUser_throwValidateException_nullEmail() {
        User user = new User(null, "User1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user)
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". ");
    }

    @Test
    void validateUser_throwValidateException_nullLogin() {
        User user = new User("Email@email.ru", null, "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user)
        );
        assertEquals(exception.getMessage(),
                "Логин не может быть пустым или содержать пробелы. ");
    }

    @Test
    void validateUser_throwValidateException_loginWithSpace() {
        User user = new User("Email@email.ru", "Login 1", "Юзер 1", "1994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user)
        );
        assertEquals(exception.getMessage(),
                "Логин не может быть пустым или содержать пробелы. ");
    }

    @Test
    void validateUser_throwValidateException_withFutureBirthday() {
        User user = new User("Email@email.ru", "Login", "Юзер 1", "2994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> UserValidator.validate(user)
        );
        assertEquals(exception.getMessage(),
                "Дата рождения не может быть в будущем. ");
    }

    @Test
    void validateUser_throwManyMessageOfValidateException_wrongUser() {
        User user = new User(null, null, "Юзер 1", "2994-11-25");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> {
                    UserValidator.validate(user);
                    user.setId(12);
                    users.put(user.getId(), user);
                    }
        );
        assertEquals(exception.getMessage(),
                "Почта должна быть заполнена и содержать символ \"@\". " +
                        "Логин не может быть пустым или содержать пробелы. " +
                        "Дата рождения не может быть в будущем. ");
    }

    @Test
    void validateUser_addingUser_correctlyUser() {
        User user = new User("Email@email.ru", "User1", "Юзер 1", "1994-11-25");
        UserValidator.validate(user);
        user.setId(25);
        users.put(user.getId(), user);
        assertEquals(user, users.get(25));
    }
}