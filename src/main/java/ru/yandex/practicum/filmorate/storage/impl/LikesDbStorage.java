package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.Collection;

@Slf4j
@Component("likesDb")
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void like(Long filmId, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO \"likes\" (\"film_id\", \"user_id\") VALUES (?, ?)",
                filmId, userId);
    }

    @Override
    public void dislike(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM \"likes\" " +
                "WHERE \"film_id\" = ? AND \"user_id\" = ? ",
                filmId, userId);

    }

    @Override
    public Collection<Long> getUserLikes(Long userId) {

        return jdbcTemplate.query(
                "SELECT * FROM \"likes\" " +
                "WHERE \"user_id\" = ?",
                (rs, rowNum) -> rs.getLong("film_id"), userId);
    }

    @Override
    public Collection<Long> getFilmLikes(Long filmId) {
        return jdbcTemplate.query(
                "SELECT * FROM \"likes\" " +
                "WHERE \"film_id\" = ?",
                (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}
