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
        user1 = new User( " ", "abc", "Igor", LocalDate.of(1991,1, 1));
        user2 = new User( "abc.ru", "abc", "Igor", LocalDate.of(1991,1, 1));
        user3 = new User( "abc@yandex.ru", "", "Igor", LocalDate.of(1991,1, 1));
        user4 = new User( "abc@yandex.ru", "abc ", "Igor", LocalDate.of(1991,1, 1));
        user5 = new User( "abc@yandex.ru", "abc", "Igor", LocalDate.of(2991,1, 1));
        user6 = new User( "abc@yandex.ru", "abc", "Igor", LocalDate.of(1991,1, 1));
        user7 = new User( null, "abc", "Igor", LocalDate.of(1991,1, 1));
        user8 = new User( "abc@yandex.ru", null, "Igor", LocalDate.of(1991,1, 1));
        user9 = new User( "abc@yandex.ru", "abc", null, LocalDate.of(1991,1, 1));
        user10 = new User( "abc@yandex.ru", "abc", "Igor", null);
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
