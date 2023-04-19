package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.DataSource;
import java.util.*;

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
    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM USERS " +
                        "ORDER BY USER_ID;",
                userMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM USERS " +
                            "WHERE USER_ID = :USER_ID;",
                    new MapSqlParameterSource()
                            .addValue("USER_ID", id),
                    userMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findFriends(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM USERS U " +
                        "JOIN FRIENDSHIPS FS ON U.USER_ID = FS.FRIEND_ID " +
                        "WHERE FS.USER_ID = :USER_ID " +
                        "ORDER BY U.USER_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", id),
                userMapper);
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long friendId) {
        return jdbcTemplate.query(
                "SELECT * FROM USERS U, " +
                        "FRIENDSHIPS F, " +
                        "FRIENDSHIPS O " +
                        "WHERE U.USER_ID = F.FRIEND_ID " +
                        "AND U.USER_ID = O.FRIEND_ID " +
                        "AND F.USER_ID = :USER_ID " +
                        "AND O.USER_ID = :FRIEND_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId),
                userMapper);
    }

    @Override
    public Optional<User> create(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        long id = insert
                .executeAndReturnKey(getUserParams(user))
                .longValue();
        return findById(id);
    }

    @Override
    public Optional<User> update(User user) {
        jdbcTemplate.update(
                "UPDATE USERS " +
                        "SET EMAIL = :EMAIL, LOGIN = :LOGIN, " +
                        "USER_NAME = :USER_NAME, BIRTHDAY = :BIRTHDAY " +
                        "WHERE USER_ID = :USER_ID;",
                getUserParams(user));
        return findById(user.getId());
    }

    @Override
    public List<Film> findRecommendedFilms(Long userId) {
        String sql =
                "SELECT F.FILM_ID, " +
                        "FILM_NAME, " +
                        "DESCRIPTION, " +
                        "RELEASE_DATE, " +
                        "DURATION, " +
                        "COUNT(USER_ID) RATE, " +
                        "F.RATING_ID, " +
                        "RATING_NAME " +
                        "FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN LIKES L ON L.FILM_ID = F.FILM_ID " +
                        "WHERE F.FILM_ID IN (SELECT LM.FILM_ID FROM LIKES LM " +
                        "JOIN (SELECT LC.USER_ID FROM LIKES LB " +
                        "LEFT JOIN LIKES LC ON LB.FILM_ID = LC.FILM_ID " +
                        "WHERE LB.USER_ID = :USER_ID AND LC.USER_ID != LB.USER_ID " +
                        "GROUP BY LC.USER_ID " +
                        "ORDER BY COUNT(LC.FILM_ID) DESC LIMIT 1) LA ON LM.USER_ID = LA.USER_ID " +
                        "WHERE LM.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES " +
                        "WHERE USER_ID = :USER_ID))" +
                        "GROUP BY F.FILM_ID;";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId),
                FilmDbStorage.filmMapper);
    }

    @Override
    public void remove(Long userId) {
        jdbcTemplate.update(
                "DELETE FROM USERS " +
                        "WHERE USER_ID = :USER_ID",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId));
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
