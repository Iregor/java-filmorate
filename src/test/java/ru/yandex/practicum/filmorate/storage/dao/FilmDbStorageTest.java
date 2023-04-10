package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    @Qualifier("filmDb")
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");
    }

    @Test
    void findAll_return5Films_adding5Films() {
        addData();
        Collection<Film> collection = filmStorage.findAll();
        assertThat(collection.size()).isEqualTo(5);
        assertThat(collection).asList().containsAnyOf(
                new Film(1L, "Евангелион 3.0+1.0", "Мехи, гиганты и тд",
                        "2021-03-08", 155, new Mpa(1L, "G")),

                new Film(2L, "Карты, деньги, два ствола", "Стейтем не бьет морды",
                        "1998-08-23", 107, new Mpa(5L, "NC-17")),

                new Film(3L, "Большой куш'", "Борис Бритва вещает про надежность большого и тяжелого",
                        "2000-08-23", 104, new Mpa(2L, "PG")),

                new Film(4L, "Побег из Шоушенка",
                        "Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены",
                        "1994-09-24", 142, new Mpa(3L, "PG-13")),

                new Film(5L, "Аватар", "Синие голые чуваки бегают по лесу",
                        "2009-12-10", 162, new Mpa(4L, "R")));
    }

    @Test
    void findById_returnFilmId1_adding5Films() {
        addData();
        assertThat(filmStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Евангелион 3.0+1.0")
                                .hasFieldOrPropertyWithValue("description", "Мехи, гиганты и тд")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2021-03-08"))
                                .hasFieldOrPropertyWithValue("duration", 155)
                                .hasFieldOrPropertyWithValue("mpa", new Mpa(1L, "G"))
                );
    }

    @Test
    void create_returnNewFilmId6_AllFilm() {
        addData();
        Film newFilm = filmStorage.create(new Film("Джентльмены",
                "Один ушлый американец ещё со студенческих лет приторговывал наркотиками",
                "2019-12-03", 113, new Mpa(2L, "PG"))).get();
        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 6L)
                .hasFieldOrPropertyWithValue("name", "Джентльмены")
                .hasFieldOrPropertyWithValue("description",
                        "Один ушлый американец ещё со студенческих лет приторговывал наркотиками")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2019-12-03"))
                .hasFieldOrPropertyWithValue("duration", 113)
                .hasFieldOrPropertyWithValue("rate", 0)
                .hasFieldOrPropertyWithValue("mpa", new Mpa(2L, "PG"));
    }

    @Test
    void update_returnUpdateFilmId4_AllFilm() {
        addData();
        filmStorage.update(new Film(4L, "Updated Name",
                "Updated descr",
                "2019-12-03", 113, new Mpa(2L, "PG")));
        assertThat(filmStorage.findById(4L))
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("name", "Updated Name")
                                .hasFieldOrPropertyWithValue("description",
                                        "Updated descr")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2019-12-03"))
                                .hasFieldOrPropertyWithValue("duration", 113)
                                .hasFieldOrPropertyWithValue("mpa", new Mpa(2L, "PG"))
                );
    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO FILMS (RATING_ID, FILM_NAME, DESCRIPTION, " +
                "RELEASE_DATE, DURATION) " +
                "VALUES (1,'Евангелион 3.0+1.0', 'Мехи, гиганты и тд'," +
                " '2021-03-08', 155)," +
                "(5,'Карты, деньги, два ствола', 'Стейтем не бьет морды, '," +
                " '1998-08-23', 107)," +
                "(2,'Большой куш', 'Борис Бритва вещает про надежность большого и тяжелого'," +
                " '2000-08-23', 104)," +
                "(3,'Побег из Шоушенка', 'Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены'," +
                " '1994-09-24', 142)," +
                "(4 ,'Аватар', 'Синие голые чуваки бегают по лесу'," +
                " '2009-12-10', 162);");
    }
}
