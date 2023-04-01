/*
package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    @Qualifier("genreDb")
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM \"genres\" ");
        jdbcTemplate.execute("ALTER TABLE \"genres\" ALTER COLUMN GENRE_ID RESTART WITH 1 ");
    }

    @Test
    void findAll_return6Genres_adding6Genres() {
        addData();
        Collection<Genre> collection = genreStorage.readAll();
        assertThat(collection.size()).isEqualTo(6);
        assertThat(collection).asList().containsAnyOf(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"),
                new Genre(3L, "Мультфильм"),
                new Genre(4L, "Триллер"),
                new Genre(5L, "Документальный"),
                new Genre(6L, "Боевик"));
    }

    @Test
    void findById_returnGenreId1_adding6Genres() {
        addData();
        assertThat(genreStorage.readById(1L))
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    void create_returnNewGenreId7_AllGenre() {
        addData();
        Genre newGenre = genreStorage.create(new Genre("Музыкальная комедия эротического содержания"));
        assertThat(newGenre).hasFieldOrPropertyWithValue("id", 7L)
                .hasFieldOrPropertyWithValue("name", "Музыкальная комедия эротического содержания");
    }

    @Test
    void update_returnUpdateGenreId5_AllGenre() {
        addData();
        genreStorage.update(new Genre(5L, "Музыкальная комедия"));
        assertThat(genreStorage.readById(5L))
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 5L)
                                .hasFieldOrPropertyWithValue("name", "Музыкальная комедия")
                );

    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO \"genres\" (\"genre_name\") " +
                "VALUES ('Комедия'), " +
                "('Драма'), " +
                "('Мультфильм'), " +
                "('Триллер'), " +
                "('Документальный'), " +
                "('Боевик')");
    }
}*/
