package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerValidationTest {

    private static Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }
    UserController userController;
    Set<ConstraintViolation<User>> violations;
    User user1, user2, user3, user4, user5, user6, user7, user8, user9, user10;
    ResponseStatusException valExc;

    @BeforeEach
    public void createTestInitialData() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));

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

        violations = validator.validate(user1);
        assertEquals(1, violations.size());

        violations = validator.validate(user2);
        assertEquals(1, violations.size());

        violations = validator.validate(user7);
        assertEquals(1, violations.size());
    }

    @Test
    public void loginValidationTest(){
        violations = validator.validate(user3);
        assertEquals(2, violations.size());

        violations = validator.validate(user4);
        assertEquals(1, violations.size());

        violations = validator.validate(user8);
        assertEquals(1, violations.size());
    }

    @Test
    public void birthValidationTest(){
        violations = validator.validate(user5);
        assertEquals(1, violations.size());

        violations = validator.validate(user10);
        assertEquals(1, violations.size());
    }

    @Test
    public void correctUserValidationTest() {
        violations = validator.validate(user6);
        assertEquals(0, violations.size());

        violations = validator.validate(user9);
        assertEquals(0, violations.size());
    }
}
