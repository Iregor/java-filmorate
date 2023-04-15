package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository("likesDb")
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    static final ResultSetExtractor<Map<Long, Set<Long>>> filmLikesExtractor = rs -> {
        Map<Long, Set<Long>> filmLikes = new HashMap<>();
        while (rs.next()) {
            filmLikes.putIfAbsent(rs.getLong("FILM_ID"), new HashSet<>());
            filmLikes.get(rs.getLong("FILM_ID"))
                    .add(rs.getLong("USER_ID"));
        }
        return filmLikes;
    };
    static final ResultSetExtractor<Map<Long, Set<Long>>> userLikesExtractor = rs -> {
        Map<Long, Set<Long>> userLikes = new HashMap<>();
        while (rs.next()) {
            userLikes.putIfAbsent(rs.getLong("USER_ID"), new HashSet<>());
            userLikes.get(rs.getLong("USER_ID"))
                    .add(rs.getLong("FILM_ID"));
        }
        return userLikes;
    };

    @Override
    public Map<Long, Set<Long>> findByFilms(Set<Long> filmIds) {
        SqlParameterSource ids = new MapSqlParameterSource("IDS", filmIds);
        return jdbcTemplate.query(
                "SELECT * FROM LIKES " +
                        "WHERE FILM_ID IN (:IDS) " +
                        "ORDER BY USER_ID;",
                ids,
                filmLikesExtractor);
    }

    @Override
    public Map<Long, Set<Long>> findByUsers(Set<Long> userIds) {
        SqlParameterSource ids = new MapSqlParameterSource("IDS", userIds);
        return jdbcTemplate.query(
                "SELECT * FROM LIKES " +
                        "WHERE USER_ID IN (:IDS) " +
                        "ORDER BY FILM_ID;",
                ids,
                userLikesExtractor);
    }

    @Override
    public void add(Long filmId, Long userId) {
        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM LIKES " +
                        "WHERE FILM_ID = :FILM_ID AND USER_ID = :USER_ID;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("USER_ID", userId),
                Integer.class);

        if (count != 0) {
            return;
        }

        jdbcTemplate.update(
                "INSERT INTO LIKES VALUES (:FILM_ID, :USER_ID);",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("USER_ID", userId));
    }

    @Override
    public void remove(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM LIKES " +
                        "WHERE FILM_ID = :FILM_ID AND USER_ID = :USER_ID;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("USER_ID", userId));
    }
}
