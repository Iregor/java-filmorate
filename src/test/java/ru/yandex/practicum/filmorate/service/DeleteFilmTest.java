package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DeleteFilmTest {

    private final FilmService filmService;
    private final DirectorService directorService;
    private final GenreService genreService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM FRIENDSHIPS ");
        jdbcTemplate.update("DELETE FROM LIKES ");
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS ");

        jdbcTemplate.update("DELETE FROM DIRECTORS ");
        jdbcTemplate.execute("ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1 ");

        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");

        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
    }

    @Test
    void delete3Films_return2Films_added5Films() {
        addData();
        assertThat(filmService.findAll().size()).isEqualTo(5);
        Film firstFilm = filmService.findById(1L);
        Film thirdFilm = filmService.findById(3L);

        filmService.like(4L, 2L);
        filmService.like(5L, 4L);

        Director director = new Director(1L, "Виктор Корнеплод");

        directorService.createDirector(director);

        Film forDeleteFilm = filmService.findById(2L);
        forDeleteFilm.setGenres(Set.of(genreService.findById(1L),
                genreService.findById(5L)));

        forDeleteFilm.setDirectors(Set.of(directorService.getById(1L)));

        filmService.update(forDeleteFilm);

        filmService.delete(2L);
        filmService.delete(4L);
        filmService.delete(5L);
        Collection<Film> collection = filmService.findAll();
        assertThat(collection.size()).isEqualTo(2);
        assertThat(collection).asList().containsAnyOf(firstFilm, thirdFilm);
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