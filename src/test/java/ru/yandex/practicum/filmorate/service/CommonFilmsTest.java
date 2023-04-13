package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommonFilmsTest {

    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM LIKES ");

        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");

        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
    }

    @Test
    void getCommonFilms_return2Films_2usersLikes3Films() {
        addData();

        filmService.like(1L, 1L);
        filmService.like(2L, 1L);
        filmService.like(3L, 1L);

        filmService.like(2L, 3L);
        filmService.like(3L, 3L);
        filmService.like(4L, 3L);

        Film firstFilm = new Film(2L, "Карты, деньги, два ствола", "Стейтем не бьет морды",
                "1998-08-23", 107, new Mpa(5L, "NC-17"));
        firstFilm.setRate(2);
        firstFilm.setLikes(Set.of(1L, 3L));


        Film thirdFilm = new Film(3L, "Большой куш'", "Борис Бритва вещает про надежность большого и тяжелого",
                "2000-08-23", 104, new Mpa(2L, "PG"));
        thirdFilm.setLikes(Set.of(1L, 3L));
        thirdFilm.setRate(2);

        Collection<Film> collection = filmService.getCommonFilms(1L, 3L);
        assertThat(collection.size()).isEqualTo(2);
        assertThat(collection).asList().containsAnyOf(firstFilm, thirdFilm);
    }

    @Test
    void getCommonFilms_return1Film_2usersLikes3FilmsAndUserDislikeFilm() {
        addData();

        filmService.like(1L, 1L);
        filmService.like(2L, 1L);
        filmService.like(3L, 1L);

        filmService.like(2L, 3L);
        filmService.like(3L, 3L);
        filmService.like(4L, 3L);

        Film firstFilm = new Film(2L, "Карты, деньги, два ствола", "Стейтем не бьет морды",
                "1998-08-23", 107, new Mpa(5L, "NC-17"));
        firstFilm.setRate(2);
        firstFilm.setLikes(Set.of(1L, 3L));

        Film thirdFilm = new Film(3L, "Большой куш'", "Борис Бритва вещает про надежность большого и тяжелого",
                "2000-08-23", 104, new Mpa(2L, "PG"));
        thirdFilm.setLikes(Set.of(1L, 3L));
        thirdFilm.setRate(2);

        Collection<Film> collection1 = filmService.getCommonFilms(1L, 3L);
        assertThat(collection1.size()).isEqualTo(2);
        assertThat(collection1).asList().containsAnyOf(firstFilm, thirdFilm);

        filmService.dislike(3L, 1L);

        Collection<Film> collection2 = filmService.getCommonFilms(1L, 3L);
        assertThat(collection2.size()).isEqualTo(1);
        assertThat(collection2).asList().containsAnyOf(firstFilm);
    }

    @Test
    void getTwoMoviesBySearchInTitle() {
        addData();
        Collection<Film> collection = filmService.searchFilms("шО", List.of("title"));
        assertThat(collection.size()).isEqualTo(2);

        Film film1 = filmService.findById(3L);
        Film film2 = filmService.findById(4L);

        assertThat(collection).asList().contains(film1,film2);
        assertTrue(film1.getName().toLowerCase().contains("шо"));
        assertTrue(film2.getName().toLowerCase().contains("шо"));
    }


    @Test
    void noFoundBySearch() throws IncorrectObjectIdException {
        addData();
        final IncorrectObjectIdException exception = assertThrows(
                IncorrectObjectIdException.class, () -> filmService.searchFilms("какой то текст", List.of("title")));
        assertEquals("Films are not found.", exception.getMessage());

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
    }
}
