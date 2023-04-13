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
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM GENRES ");
        jdbcTemplate.execute("ALTER TABLE GENRES ALTER COLUMN GENRE_ID RESTART WITH 1 ");
    }

    @Test
    void findAll_return6Genres_adding6Genres() {
        addData();
        Collection<Genre> collection = genreStorage.findAll();
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
        assertThat(genreStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }


    private void addData() {
        jdbcTemplate.update("INSERT INTO GENRES (GENRE_NAME) " +
                "VALUES ('Комедия'), " +
                "('Драма'), " +
                "('Мультфильм'), " +
                "('Триллер'), " +
                "('Документальный'), " +
                "('Боевик')");
    }
}

