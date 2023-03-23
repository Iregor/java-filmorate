package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    @Qualifier("mpaDb") private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void BeforeEach() {
        jdbcTemplate.update("DELETE FROM \"rating_mpa\" ");
        jdbcTemplate.execute("ALTER TABLE \"rating_mpa\" ALTER COLUMN \"rating_id\" RESTART WITH 1 ");
    }

    @Test
    void findAll_return5Mpa_adding5Mpa() {
        addData();

        Collection<Mpa> collection = mpaStorage.findAll();
        assertThat(collection.size()).isEqualTo(5);
        assertThat(collection).asList().containsAnyOf(
                new Mpa(1L, "G"),
                new Mpa(2L, "PG"),
                new Mpa(3L, "PG-13"),
                new Mpa(4L, "R"),
                new Mpa(5L, "NC-17"));
    }

    @Test
    void findById_returnMpaId1_adding5Mpa() {
        addData();
        assertThat(mpaStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    void create_returnNewMpaId6_AllMpa() {
        addData();
        Mpa newMpa = mpaStorage.create(new Mpa("New Rating"));
        assertThat(newMpa).hasFieldOrPropertyWithValue("id", 6L)
                .hasFieldOrPropertyWithValue("name", "New Rating");
    }

    @Test
    void update_returnUpdateMpaId4_AllGenre() {
        addData();
        mpaStorage.update((new Mpa(4L, "Update Rating")));
        assertThat(mpaStorage.findById(4L))
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("name", "Update Rating")
                );

    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO \"rating_mpa\" (\"name\") " +
                "VALUES ('G'), " +
                "('PG')," +
                "('PG-13')," +
                "('R')," +
                "('NC-17')");

        jdbcTemplate.execute("ALTER TABLE \"genres\" ALTER COLUMN \"genre_id\" " +
                "RESTART WITH (SELECT MAX( \"genre_id\") FROM \"genres\") + 1 ");
    }
}