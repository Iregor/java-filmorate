package ru.yandex.practicum.filmorate;

import controller.UserController;
import exceptions.ValidationException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerValidationTest {

    UserController userController;
    User user1, user2, user3, user4, user5, user6, user7, user8, user9, user10;
    ResponseStatusException valExc;

    @BeforeEach
    public void createTestInitialData() {
        userController = new UserController();

        user1 = User.builder()
                .email("")
                .login("abc")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user2 = User.builder()
                .email("abc.ru")
                .login("abc")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user3 = User.builder()
                .email("abc@yandex.ru")
                .login("")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user4 = User.builder()
                .email("abc@yandex.ru")
                .login("abc ")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user5 = User.builder()
                .email("abc@yandex.ru")
                .login("abc")
                .name("Igor")
                .birthday(LocalDate.of(2991,1, 1))
                .build();

        user6 = User.builder()
                .email("abc@yandex.ru")
                .login("abc")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user7 = User.builder()
                .email(null)
                .login("abc")
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user8 = User.builder()
                .email("abc@yandex.ru")
                .login(null)
                .name("Igor")
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user9 = User.builder()
                .email("abc@yandex.ru")
                .login("abc")
                .name(null)
                .birthday(LocalDate.of(1991,1, 1))
                .build();

        user10 = User.builder()
                .email("abc@yandex.ru")
                .login("abc")
                .name("Igor")
                .birthday(null)
                .build();
    }

    @Test
    public void emailValidationTest(){

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user1));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user2));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user7));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));
    }

    @Test
    public void loginValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user3));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user4));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user8));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));
    }

    @Test
    public void birthValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user5));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));

        valExc = assertThrows(ResponseStatusException.class, () -> userController.add(user10));
        assertTrue(valExc.getMessage().contains("Ошибка валидации пользователя."));
    }

    @Test
    public void correctUserValidationTest() throws ValidationException{
        userController.add(user6);
        assertTrue(userController.users().contains(user6));

        userController.add(user9);
        assertTrue(userController.users().contains(user9));
    }
}
