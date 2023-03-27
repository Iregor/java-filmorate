package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Optional;

@Slf4j
@Component("friendDb")
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void writeRow(Long userId, Long friendId) {
        Optional<Boolean> status = readFriendshipStatus(userId, friendId);

        if (status.isPresent() && !status.get()) {
            jdbcTemplate.update(
                    "UPDATE \"friendship\" " +
                            "SET \"status\" = true " +
                            "WHERE \"user_id\" = ?" +
                            "AND \"friend_id\" = ?",
                    friendId, userId);
        } else {
            jdbcTemplate.update(
                    "INSERT INTO \"friendship\" (\"user_id\", \"friend_id\", \"status\") VALUES (?, ?, false)",
                    userId, friendId);
        }
    }

    @Override
    public void deleteRow(Long userId, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM \"friendship\" WHERE \"user_id\" = ? AND \"friend_id\" = ? ",
                userId, friendId);
    }

    private Optional<Boolean> readFriendshipStatus(Long userId, Long friendId) {
        SqlRowSet statusRow = jdbcTemplate.queryForRowSet(
                "SELECT \"status\" " +
                        "FROM \"friendship\" " +
                        "WHERE \"user_id\" = ? and \"friend_id\" = ?", friendId, userId);
        if (statusRow.next()) {
            return Optional.of(statusRow.getBoolean("status"));
        } else {
            return Optional.empty();
        }
    }
}
