package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("userDb")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    static final RowMapper<User> userMapper =
            (rs, rowNum) -> User.builder()
                    .id(rs.getLong("USER_ID"))
                    .email(rs.getString("EMAIL"))
                    .login(rs.getString("LOGIN"))
                    .name(rs.getString("USER_NAME"))
                    .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                    .friends(new HashSet<>())
                    .likeFilms(new HashSet<>())
                    .build();

    @Override
    public Collection<User> findAll() {
        Collection<User> result = jdbcTemplate.query(
                "SELECT * FROM USERS ORDER BY USER_ID;",
                userMapper);
        findUserData(result);
        return result;
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            Optional<User> result = Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM USERS WHERE USER_ID = :USER_ID;",
                    new MapSqlParameterSource()
                            .addValue("USER_ID", id),
                    userMapper));
            result.ifPresent(u -> findUserData(List.of(u)));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> findFriends(Long id) {
        Collection<User> result = jdbcTemplate.query(
                "SELECT * FROM USERS U " +
                        "JOIN FRIENDSHIPS FS ON U.USER_ID = FS.FRIEND_ID " +
                        "WHERE FS.USER_ID = :USER_ID " +
                        "ORDER BY U.USER_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", id),
                userMapper);
        findUserData(result);
        return result;
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long friendId) {
        Collection<User> result = jdbcTemplate.query(
                "SELECT * FROM USERS U, FRIENDSHIPS F, FRIENDSHIPS O " +
                        "WHERE U.USER_ID = F.FRIEND_ID AND U.USER_ID = O.FRIEND_ID " +
                        "AND F.USER_ID = :USER_ID AND O.USER_ID = :FRIEND_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId),
                userMapper);
        findUserData(result);
        return result;
    }

    @Override
    public Optional<User> create(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        long id = insert
                .executeAndReturnKey(getUserParams(user))
                .longValue();
        Optional<User> result = findById(id);
        result.ifPresent(u -> findUserData(List.of(u)));
        return result;
    }

    @Override
    public Optional<User> update(User user) {
        jdbcTemplate.update(
                "UPDATE USERS " +
                        "SET EMAIL = :EMAIL, LOGIN = :LOGIN, " +
                        "USER_NAME = :USER_NAME, BIRTHDAY = :BIRTHDAY " +
                        "WHERE USER_ID = :USER_ID;",
                getUserParams(user));
        Optional<User> result = findById(user.getId());
        result.ifPresent(u -> findUserData(List.of(u)));
        return result;
    }

    private void findUserData(Collection<User> users) {
        Map<Long, User> usersMap = users
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        SqlParameterSource ids = new MapSqlParameterSource("IDS", usersMap.keySet());

        jdbcTemplate.query(
                "SELECT U.USER_ID F_ID, FS.USER_ID U_ID FROM USERS U " +
                        "JOIN FRIENDSHIPS FS ON U.USER_ID = FS.FRIEND_ID " +
                        "WHERE FS.USER_ID IN (:IDS) " +
                        "ORDER BY U.USER_ID;",
                ids,
                (rs, rowNum) -> usersMap
                        .get(rs.getLong("U_ID"))
                        .getFriends()
                        .add(rs.getLong("F_ID")));

        jdbcTemplate.query(
                "SELECT * FROM LIKES " +
                        "WHERE USER_ID IN (:IDS) " +
                        "ORDER BY FILM_ID;",
                ids,
                (rs, rowNum) -> usersMap
                        .get(rs.getLong("USER_ID"))
                        .getLikeFilms()
                        .add(rs.getLong("FILM_ID")));
    }

    private MapSqlParameterSource getUserParams(User user) {
        return new MapSqlParameterSource()
                .addValue("USER_ID", user.getId())
                .addValue("EMAIL", user.getEmail())
                .addValue("LOGIN", user.getLogin())
                .addValue("USER_NAME", user.getName())
                .addValue("BIRTHDAY", user.getBirthday());
    }
}
