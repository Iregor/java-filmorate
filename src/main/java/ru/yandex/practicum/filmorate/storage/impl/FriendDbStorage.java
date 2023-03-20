package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("friendDb")
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Long> getFriends(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM \"friendship\" " +
                "WHERE \"user_id\" = ?"
                , (rs, rowNum) -> rs.getLong("friend_id"), userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<Boolean> status = getFriendshipStatus(userId, friendId);

        if (status.isPresent() && !status.get()) { // если friendId когда-то отправлял запрос, а userId его не подтвердил, то подтверждение
            jdbcTemplate.update(
                    "UPDATE \"friendship\" " +
                            "SET \"status\" = true " +
                            "WHERE \"user_id\" = ?" +
                            "AND \"friend_id\" = ?",
                    friendId, userId);
        } else { // или направление нового запроса от userId к friendId
            jdbcTemplate.update(
                    "INSERT INTO \"friendship\" (\"user_id\", \"friend_id\", \"status\") VALUES (?, ?, false)",
                    userId, friendId);
        }
    }

    @Override
    public void delFriend(Long userId, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM \"friendship\" WHERE \"user_id\" = ? AND \"friend_id\" = ? ",
                userId, friendId);
    }

    @Override
    public Collection<Long> getCommonFriends(Long userId, Long friendId) {
        String sql =
                "SELECT u.\"user_id\" " +
                "FROM (SELECT \"friend_id\" AS friend_id1 FROM \"friendship\" " +
                        "WHERE \"user_id\" = ? " +
                        "UNION SELECT \"user_id\" " +
                        "FROM \"friendship\" " +
                        "WHERE \"friend_id\" = ?) AS f1 " +
                "JOIN (SELECT \"friend_id\" AS friend_id2 " +
                        "FROM \"friendship\" " +
                        "WHERE \"user_id\" = ? " +
                        "UNION SELECT \"user_id\" " +
                        "FROM \"friendship\" " +
                        "WHERE \"friend_id\" = ?) AS f2 ON f2.friend_id2 = f1.friend_id1 " +
                        "JOIN \"users\" AS u ON u.\"user_id\" = f1.friend_id1 " +
                        "ORDER BY u.\"user_id\" ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId, friendId, friendId);
    }

    private Optional<Boolean> getFriendshipStatus(Long userId, Long friendId) {
        SqlRowSet statusRow = jdbcTemplate.queryForRowSet(
                "SELECT \"status\" " +
                        "FROM \"friendship\" " +
                        "WHERE \"user_id\" = ? and \"friend_id\" = ?" , friendId, userId);
        if(statusRow.next()) {
            return Optional.of(statusRow.getBoolean("status"));
        } else {
            return Optional.empty();
        }
    }
}
