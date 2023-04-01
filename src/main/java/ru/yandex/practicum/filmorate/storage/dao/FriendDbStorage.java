package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

@Slf4j
@Repository("friendDb")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void add(Long userId, Long friendId) {
        jdbcTemplate.update(
                "INSERT INTO FRIENDSHIPS VALUES (:USER_ID, :FRIEND_ID, false);",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId));
    }

    @Override
    public void remove(Long userId, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM FRIENDSHIPS WHERE USER_ID = :USER_ID AND FRIEND_ID = :FRIEND_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId));
    }
}
