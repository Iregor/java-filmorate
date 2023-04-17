package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository("friendDb")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    static final ResultSetExtractor<Map<Long, Set<Long>>> friendsExtractor = rs -> {
        Map<Long, Set<Long>> filmLikes = new HashMap<>();
        while (rs.next()) {
            filmLikes.putIfAbsent(rs.getLong("U_ID"), new HashSet<>());
            filmLikes.get(rs.getLong("U_ID"))
                    .add(rs.getLong("F_ID"));
        }
        return filmLikes;
    };

    @Override
    public Map<Long, Set<Long>> findByUsers(Set<Long> userIds) {
        SqlParameterSource ids = new MapSqlParameterSource("IDS", userIds);
        return jdbcTemplate.query(
                "SELECT U.USER_ID F_ID, " +
                        "FS.USER_ID U_ID FROM USERS U " +
                        "JOIN FRIENDSHIPS FS ON U.USER_ID = FS.FRIEND_ID " +
                        "WHERE FS.USER_ID IN (:IDS) " +
                        "ORDER BY U.USER_ID;",
                ids,
                friendsExtractor);
    }

    @Override
    public void add(Long userId, Long friendId) {
        jdbcTemplate.update(
                "INSERT INTO FRIENDSHIPS " +
                        "VALUES (:USER_ID, :FRIEND_ID, false);",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId));
    }

    @Override
    public void remove(Long userId, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM FRIENDSHIPS " +
                        "WHERE USER_ID = :USER_ID " +
                        "AND FRIEND_ID = :FRIEND_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId));
    }
}
