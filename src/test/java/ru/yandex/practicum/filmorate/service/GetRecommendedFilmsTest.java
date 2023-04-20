package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetRecommendedFilmsTest {

    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM LIKES;");
        jdbcTemplate.update("DELETE FROM FILM_GENRES;");

        jdbcTemplate.update("DELETE FROM FILMS;");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;");

        jdbcTemplate.update("DELETE FROM USERS;");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;");
    }

    @Test
    void getRecommendedFilms_return1Films_added3filmsAndUsers() {
        addData();
        User user1 = userService.findById(1L);
        User user2 = userService.findById(2L);
        User user3 = userService.findById(3L);

        Film film1 = filmService.findById(1L);
        Film film2 = filmService.findById(2L);
        Film film3 = filmService.findById(3L);

        filmService.like(user1.getId(), film1.getId());
        filmService.like(user1.getId(), film3.getId());
        filmService.like(user2.getId(), film2.getId());
        filmService.like(user2.getId(), film3.getId());
        filmService.like(user3.getId(), film1.getId());
        filmService.like(user3.getId(), film2.getId());
        filmService.like(user3.getId(), film3.getId());

        assertThat(userService.getRecommendedFilms(user1.getId()).size()).isEqualTo(1);
        assertThat(userService.getRecommendedFilms(user1.getId())).asList().contains(filmService.findById(2L));
        assertThat(userService.getRecommendedFilms(user2.getId()).size()).isEqualTo(1);
        assertThat(userService.getRecommendedFilms(user2.getId())).asList().contains(filmService.findById(1L));
        assertThat(userService.getRecommendedFilms(user3.getId()).size()).isEqualTo(0);
    }

    private void addData() {
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

        jdbcTemplate.update("INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY ) " +
                "VALUES ('email@yandex.ru', 'trabecula', 'Tr-exo', '2011-03-08')," +
                "('ema@mail.ru', 'login', 'Name', '2001-06-05')," +
                "('ema@yahoo.ru', 'originator', 'SurName', '1988-01-02')," +
                "('ail@rambler.ru', 'user34321', 'User', '2021-03-18')," +
                "('eml@ms.ru', 'poison', 'Dbname', '1994-11-25')");

        jdbcTemplate.update("INSERT INTO FILM_GENRES " +
                "VALUES (1, 1), (1, 2), (2, 2), (2, 3), (3, 3), (3, 4) ;");
    }
}
