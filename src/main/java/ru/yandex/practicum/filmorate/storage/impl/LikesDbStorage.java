package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.time.LocalDate;

@Repository("likesDb")
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void add(Long filmId, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO LIKES VALUES (:FILM_ID, :USER_ID, :DATE)",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("USER_ID", userId)
                        .addValue("DATE", LocalDate.now()));
    }

    @Override
    public void remove(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM LIKES " +
                        "WHERE FILM_ID = :FILM_ID AND USER_ID = :USER_ID ",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("USER_ID", userId));
    }
}
