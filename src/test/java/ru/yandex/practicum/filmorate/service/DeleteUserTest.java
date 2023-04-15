package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DeleteUserTest {

    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM LIKES ");

        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
    }

    @Test
    void delete3Users_return2Users_added5Users() {
        addData();
        assertThat(userService.findAll().size()).isEqualTo(5);
        User firstUser = userService.findById(1L);
        User thirdUser = userService.findById(3L);
        userService.delete(2L);
        userService.delete(4L);
        userService.delete(5L);
        Collection<User> collection = userService.findAll();
        assertThat(collection.size()).isEqualTo(2);
        assertThat(collection).asList().containsAnyOf(firstUser, thirdUser);
    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY ) " +
                "VALUES ('email@yandex.ru', 'trulala', 'Trexo', '2011-03-08')," +
                "('ema@mail.ru', 'login', 'Name', '2001-06-05')," +
                "('ema@yahoo.ru', 'loginator', 'SurName', '1988-01-02')," +
                "('ail@rambler.ru', 'user34321', 'User', '2021-03-18')," +
                "('eml@ms.ru', 'kpoisk', 'Dbnjh', '1994-11-25')");
    }
}