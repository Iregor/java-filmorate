package ru.yandex.practicum.filmorate.storage.impl.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("userDb")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"users\" " +
                        "ORDER BY \"user_id\" ",
                (rs, rowNum) ->
                new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("birthday")));
    }

    @Override
    public Optional<User> findById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"users\" WHERE \"user_id\" = ?", id);
        if(userRows.next()) {
            Long user_id = userRows.getLong("user_id");
            String email = userRows.getString("email");
            String login = userRows.getString("login");
            String name = userRows.getString("name");
            String birthday = userRows.getString("birthday");
            log.info("Найден пользователь: {} {}", user_id, login);
            return Optional.of(new User(user_id, email, login, name, birthday));
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        if(user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(
                "INSERT INTO \"users\" (\"email\", \"name\", \"login\", \"birthday\")" +
                        "VALUES (?,?,?,?)",
                user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        return getUserFromDb(user);
    }

    @Override
    public User update(User user) {
        if(findById(user.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("User %d is not found.", user.getId()));
        }
        jdbcTemplate.update(
                "UPDATE \"users\" " +
                        "SET \"email\" = ?, \"name\" = ?, \"login\" = ?, \"birthday\" = ? " +
                        "WHERE \"user_id\" = ? ",
                user.getEmail(), user.getName(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }


    private User getUserFromDb(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                    "SELECT * FROM \"users\" WHERE \"login\" = ? AND \"email\" = ?",
                    user.getLogin(), user.getEmail());
        if(userRows.next()) {
            return new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getString("birthday"));
        } else {
            log.info("Данные не найдены.");
            return null;
        }
    }
}
