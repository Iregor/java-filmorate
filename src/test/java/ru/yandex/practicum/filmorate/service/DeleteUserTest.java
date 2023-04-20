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
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM FRIENDSHIPS;");
        jdbcTemplate.update("DELETE FROM LIKES;");

        jdbcTemplate.update("DELETE FROM USERS;");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;");

        jdbcTemplate.update("DELETE FROM FILMS;");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;");
    }

    @Test
    void deleteUser_return2Users_added5Users() {
        addData();
        assertThat(userService.findAll().size()).isEqualTo(5);
        User firstUser = userService.findById(1L);
        User thirdUser = userService.findById(3L);
        userService.addFriend(1L, 2L);
        userService.addFriend(3L, 5L);
        userService.addFriend(2L, 4L);

        filmService.like(1L, 2L);
        filmService.like(3L, 4L);

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

        jdbcTemplate.update("INSERT INTO FILMS (RATING_ID, FILM_NAME, DESCRIPTION, " +
                "RELEASE_DATE, DURATION) " +
                "VALUES (1,'Евангелион 3.0+1.0', 'Мехи, гиганты и тд'," +
                " '2021-03-08', 155)," +
                "(5,'Карты, деньги, два ствола', 'Стейтем не бьет морды'," +
                " '1998-08-23', 107)," +
                "(2,'Большой куш', 'Борис Бритва вещает про надежность большого и тяжелого'," +
                " '2000-08-23', 104)," +
                "(3,'Побег из Шоушенка', 'Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены'," +
                " '1994-09-24', 142)," +
                "(4 ,'Аватар', 'Синие голые чуваки бегают по лесу'," +
                " '2009-12-10', 162);");
    }
}