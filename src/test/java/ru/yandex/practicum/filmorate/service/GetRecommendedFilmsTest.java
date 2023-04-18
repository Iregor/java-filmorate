package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetRecommendedFilmsTest {

    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM LIKES ");
        jdbcTemplate.update("DELETE FROM FILM_GENRES ");

        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");

        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
    }

    @Test
    void getRecommendedFilmsIdsTest() {
        addData();
        collectLikeModel();
        assertEquals(userService.findAdviseFilmsIds(1L).size(), 3, "prediction doesn't work");
        assertTrue(userService.findAdviseFilmsIds(1L).contains(3L), "prediction doesn't work");
        filmService.like(1L, 3L);
        assertEquals(userService.findAdviseFilmsIds(1L).size(), 4, "prediction doesn't work");
        assertTrue(userService.findAdviseFilmsIds(1L).contains(1L), "prediction doesn't work");
    }

    @Test
    void getFilmsFromRecommendedFilmsIdsTest() {
        addData();
        collectLikeModel();
        filmService.like(1L, 3L);
        Collection<Long> testIdModelCollection = userService.findAdviseFilmsIds(1L);
        Map<Long, Film> predictionFilms = filmService.convertIdsToFilms(testIdModelCollection)
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        assertEquals(predictionFilms.get(1L), filmService.findById(1L), "prediction doesn't work");
        assertEquals(predictionFilms.get(3L), filmService.findById(3L), "prediction doesn't work");
    }

    private void collectLikeModel() {
        filmService.like(5L, 1L);
        filmService.like(5L, 2L);
        filmService.like(5L, 3L);
        filmService.like(4L, 1L);
        filmService.like(4L, 2L);
        filmService.like(4L, 3L);
        filmService.like(3L, 2L);
        filmService.like(3L, 3L);
        filmService.like(2L, 4L);
        filmService.like(2L, 5L);
        filmService.like(1L, 4L);
        filmService.like(1L, 5L);
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
                "VALUES ('email@yandex.ru', 'trulala', 'Trexo', '2011-03-08')," +
                "('ema@mail.ru', 'login', 'Name', '2001-06-05')," +
                "('ema@yahoo.ru', 'loginator', 'SurName', '1988-01-02')," +
                "('ail@rambler.ru', 'user34321', 'User', '2021-03-18')," +
                "('eml@ms.ru', 'kpoisk', 'Dbnjh', '1994-11-25')");

        jdbcTemplate.update("INSERT INTO FILM_GENRES " +
                "VALUES (1, 1), (1, 2), (2, 2), (2, 3), (3, 3), (3, 4) ;");
    }
}
