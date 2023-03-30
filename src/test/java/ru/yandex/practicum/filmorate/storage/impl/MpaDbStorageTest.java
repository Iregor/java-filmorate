package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    @Qualifier("mpaDb") private final MpaStorage mpaStorage;

    @Test
    void findAll_return5Mpa_adding5Mpa() {
        Collection<Mpa> collection = mpaStorage.readAll();
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
        assertThat(mpaStorage.readById(1L))
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }
}