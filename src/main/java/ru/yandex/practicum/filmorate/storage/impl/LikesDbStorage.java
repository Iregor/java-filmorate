package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.time.LocalDate;

@Slf4j
@Component("likesDb")
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void writeRow(Long filmId, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO \"likes\" (\"film_id\", \"user_id\", \"date\") VALUES (?, ?, ?)",
                filmId, userId, LocalDate.now());
        log.debug("User {} liked film {}", userId, filmId);
    }

    @Override
    public void deleteRow(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM \"likes\" " +
                "WHERE \"film_id\" = ? AND \"user_id\" = ? ",
                filmId, userId);
        log.debug("User {} disliked film {}", userId, filmId);
    }
}
