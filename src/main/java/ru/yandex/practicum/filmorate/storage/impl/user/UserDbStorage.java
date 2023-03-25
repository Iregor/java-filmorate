package ru.yandex.practicum.filmorate.storage.impl.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("userDb")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"users\" " +
                        "ORDER BY \"user_id\" ",
                (rs, rowNum) -> getUserFromResultSet(rs));
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM \"users\" u\n" +
                        "JOIN \"friendship\" f ON u.\"user_id\" = f.\"friend_id\"\n" +
                        "WHERE f.\"user_id\" = ?",
                (rs, rowNum) -> getUserFromResultSet(rs), userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        final String sql = "SELECT * FROM \"users\" AS u, \"friendship\" AS f, \"friendship\" AS o " +
                "where u.\"user_id\" = f.\"friend_id\" " +
                "AND u.\"user_id\" = o.\"friend_id\" " +
                "AND f.\"user_id\" = ? " +
                "AND o.\"user_id\" = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getUserFromResultSet(rs), userId, friendId);
    }

    @Override
    public Optional<User> findById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"users\" WHERE \"user_id\" = ?", id);
        if (userRows.next()) {
            User user = getUserFromSqlRowSet(userRows);
            log.debug("User found: {} {}", user.getId(), user.getLogin());
            return Optional.of(user);
        } else {
            log.debug("User {} is not found.", id);
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        jdbcTemplate.update(
                "INSERT INTO \"users\" (\"email\", \"login\", \"user_name\", \"birthday\" ) " +
                        "VALUES (?,?,?,?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUserFromDb(user);
    }

    @Override

    public User update(User user) {
        jdbcTemplate.update(
                "UPDATE \"users\" " +
                        "SET \"email\" = ?, \"user_name\" = ?, \"login\" = ?, \"birthday\" = ? " +
                        "WHERE \"user_id\" = ? ",
                user.getEmail(), user.getName(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    private User getUserFromDb(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"users\" WHERE \"login\" = ? AND \"email\" = ?",
                user.getLogin(), user.getEmail());
        if (userRows.next()) {
            return getUserFromSqlRowSet(userRows);
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }

    private User getUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getString("birthday"));
    }

    private User getUserFromSqlRowSet(SqlRowSet srs) {
        return new User(
                srs.getLong("user_id"),
                srs.getString("email"),
                srs.getString("login"),
                srs.getString("user_name"),
                srs.getString("birthday"));
    }
}
