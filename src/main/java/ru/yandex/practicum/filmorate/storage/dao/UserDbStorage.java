package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;
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
    public Collection<User> findFriends(Long id) {
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
    public Collection<User> findCommonFriends(Long userId, Long friendId) {
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
    public Map<Long, Integer> getFilmsScore(Long userId) {
        Map<Long, Integer> filmsScore = new HashMap<>();
        jdbcTemplate.query("SELECT l1.FILM_ID, COUNT(l1.FILM_ID) SCORE " +
                        "FROM LIKES l1 JOIN LIKES l2 ON l1.USER_ID = l2.USER_ID " +
                        "AND l2.USER_ID <> :USER_ID " +
                        "WHERE l2.FILM_ID IN ( " +
                        "SELECT FILM_ID " +
                        "FROM LIKES " +
                        "WHERE USER_ID = :USER_ID) " +
                        "GROUP BY l1.FILM_ID " +
                        "ORDER BY SCORE DESC",
                new MapSqlParameterSource("USER_ID", userId),
                (ResultSet rs) -> {
                    filmsScore.put(rs.getLong("FILM_ID"), rs.getInt("SCORE"));
                });
        return filmsScore;
    }

    @Override
    public Map<Long, List<Long>> getDiffFilms(Long userId) {
        final Map<Long, List<Long>> filmLikeByUserId = new HashMap<>();
        jdbcTemplate.query(
                "SELECT L1.USER_ID, L1.FILM_ID " +
                        "FROM LIKES L1 " +
                        "LEFT JOIN LIKES L2 ON L1.FILM_ID = L2.FILM_ID AND L2.USER_ID = :USER_ID " +
                        "WHERE L1.USER_ID <> :USER_ID AND L2.USER_ID IS NULL " +
                        "GROUP BY L1.USER_ID, L1.FILM_ID " +
                        "ORDER BY L1.USER_ID, L1.FILM_ID",
                new MapSqlParameterSource().addValue("USER_ID", userId),
                (ResultSet rs) -> {
                    filmLikeByUserId.computeIfAbsent(rs.getLong("USER_ID"), l -> new ArrayList<>())
                            .add(rs.getLong("FILM_ID"));
                });
        return filmLikeByUserId;
    }

    @Override
    public Collection<Long> convertMaxCommonLikes(Long userId) {
        final List<Long> scores = new ArrayList<>();
        final Map<Long, Long> scoreByUsersId = new HashMap<>();
        jdbcTemplate.query(
                "SELECT L.USER_ID, COUNT(LU.FILM_ID) SCORE " +
                        "FROM LIKES L JOIN LIKES LU ON L.FILM_ID = LU.FILM_ID " +
                        "WHERE L.USER_ID <> :USER_ID AND LU.USER_ID = :USER_ID " +
                        "GROUP BY L.USER_ID " +
                        "ORDER BY SCORE DESC",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId),
                (ResultSet rs) -> {
                    long score = rs.getLong("SCORE");
                    scores.add(score);
                    scoreByUsersId.put(rs.getLong("USER_ID"), score);
                });
        Optional<Long> scoreMax = scores.stream().max(Comparator.naturalOrder());
        return scoreMax.map(l -> scoreByUsersId.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(l))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
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
