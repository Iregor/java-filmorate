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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetPopularFilmTest {

    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

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
    void getPopularFilmsWith2Count_return2Films_added5FilmsWithLikes() {
        addData();
        addLikes();

        Map<Long, Film> allFilms = filmService
                .findAll()
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        Collection<Film> collection = filmService.getPopularFilms(2, null, null);
        assertThat(collection.size()).isEqualTo(2);
        assertThat(collection).asList().containsExactly(allFilms.get(5L), allFilms.get(4L));
    }

    @Test
    void getPopularFilmsWith1998Year_return1Film_added5FilmsWithLikes() {
        addData();
        addLikes();

        Map<Long, Film> allFilms = filmService
                .findAll()
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        Collection<Film> collection = filmService.getPopularFilms(10, null, "1998");
        assertThat(collection.size()).isEqualTo(1);
        assertThat(collection).asList().containsExactly(allFilms.get(2L));
    }

    @Test
    void getPopularFilmsWith3GenreId_return2Film_added5FilmsWithLikes() {
        addData();
        addLikes();

        Map<Long, Film> allFilms = filmService
                .findAll()
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        Collection<Film> collection = filmService.getPopularFilms(10, 3L, null);
        assertThat(collection.size()).isEqualTo(2);
        assertThat(collection).asList().containsExactly(allFilms.get(3L), allFilms.get(2L));
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

    private void addLikes() {
        filmService.like(5L, 1L);
        filmService.like(5L, 2L);
        filmService.like(5L, 3L);
        filmService.like(5L, 4L);
        filmService.like(5L, 5L);

        filmService.like(4L, 1L);
        filmService.like(4L, 2L);
        filmService.like(4L, 3L);
        filmService.like(4L, 4L);

        filmService.like(3L, 1L);
        filmService.like(3L, 2L);
        filmService.like(3L, 3L);

        filmService.like(2L, 1L);
        filmService.like(2L, 2L);

        filmService.like(1L, 1L);
    }
}