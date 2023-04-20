package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    static final RowMapper<Film> filmMapper =
            ((rs, rowNum) -> Film.builder()
                    .id(rs.getLong("FILM_ID"))
                    .name(rs.getString("FILM_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .rate(rs.getInt("RATE"))
                    .mpa(new Mpa(rs.getLong("RATING_ID"),
                            rs.getString("RATING_NAME")))
                    .genres(new HashSet<>())
                    .likes(new HashSet<>())
                    .directors(new HashSet<>())
                    .build());

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(
                "SELECT F.FILM_ID," +
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
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY F.FILM_ID;",
                filmMapper);
    }

    @Override
    public List<Film> findPopularFilms(int size) {
        return jdbcTemplate.query(
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
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size),
                filmMapper);
    }

    @Override
    public List<Film> searchFilmsByTitle(String subString) {
        return jdbcTemplate.query(
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
                        "WHERE  LOWER( F.FILM_NAME) LIKE LOWER(:SUBSTRING) " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC;",
                new MapSqlParameterSource()
                        .addValue("SUBSTRING", "%" + subString + "%"),
                filmMapper);
    }

    @Override
    public List<Film> searchFilmsByDirector(String subString) {
        return jdbcTemplate.query(
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
                        "LEFT JOIN (FILM_DIRECTORS FD INNER JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID) " +
                        "ON F.FILM_ID = FD.FILM_ID " +
                        "WHERE LOWER(D.DIRECTOR_NAME) LIKE LOWER(:SUBSTRING) " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC;",
                new MapSqlParameterSource()
                        .addValue("SUBSTRING", "%" + subString + "%"),
                filmMapper);
    }

    @Override
    public List<Film> findPopularFilmsByGenreId(int size, Long genreId) {
        return jdbcTemplate.query(
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
                        "JOIN FILM_GENRES FG ON F.FILM_ID = FG.FILM_ID " +
                        "WHERE GENRE_ID = :GENRE_ID " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("GENRE_ID", genreId),
                filmMapper);
    }

    @Override
    public List<Film> findPopularFilmsByYear(int size, String year) {
        return jdbcTemplate.query(
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
                        "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = :YEAR " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("YEAR", year),
                filmMapper);
    }

    @Override
    public List<Film> findPopularFilmsByGenreIdAndYear(int size, Long genreId, String year) {
        return jdbcTemplate.query(
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
                        "JOIN FILM_GENRES FG ON F.FILM_ID = FG.FILM_ID " +
                        "WHERE GENRE_ID = :GENRE_ID " +
                        "AND EXTRACT(YEAR FROM F.RELEASE_DATE) = :YEAR " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("GENRE_ID", genreId)
                        .addValue("YEAR", year),
                filmMapper);
    }

    @Override
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return jdbcTemplate.query(
                "SELECT F.FILM_ID, " +
                        "FILM_NAME, " +
                        "DESCRIPTION, " +
                        "RELEASE_DATE, " +
                        "DURATION, " +
                        "COUNT(L.USER_ID) RATE, " +
                        "F.RATING_ID, " +
                        "RATING_NAME, " +
                        "LU.USER_ID, " +
                        "LF.USER_ID " +
                        "FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN LIKES L ON L.FILM_ID = F.FILM_ID " +
                        "JOIN LIKES LU ON F.FILM_ID = LU.FILM_ID " +
                        "JOIN LIKES LF ON F.FILM_ID = LF.FILM_ID " +
                        "WHERE LU.USER_ID = :USER_ID AND " +
                        "LF.USER_ID = :FRIEND_ID " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId),
                filmMapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
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
                            "WHERE F.FILM_ID = :FILM_ID " +
                            "GROUP BY F.FILM_ID;",
                    new MapSqlParameterSource()
                            .addValue("FILM_ID", id),
                    filmMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        long id = insert
                .executeAndReturnKey(getFilmParams(film))
                .longValue();
        return findById(id);
    }

    @Override
    public Optional<Film> update(Film film) {
        jdbcTemplate.update(
                "UPDATE FILMS " +
                        "SET FILM_NAME = :FILM_NAME, " +
                        "DESCRIPTION = :DESCRIPTION, " +
                        "RELEASE_DATE = :RELEASE_DATE, " +
                        "DURATION = :DURATION, " +
                        "RATING_ID = :RATING_ID " +
                        "WHERE FILM_ID = :FILM_ID;",
                getFilmParams(film));
        return findById(film.getId());
    }

    @Override
    public void remove(Long filmId) {
        jdbcTemplate.update(
                "DELETE FROM FILMS " +
                        "WHERE FILM_ID = :FILM_ID;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId));
    }

    @Override
    public List<Film> findFilmsDirectorByYear(Long directorId) {
        return jdbcTemplate.query(
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
                        "JOIN FILM_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "WHERE FD.DIRECTOR_ID = :DIRECTOR_ID " +
                        "GROUP BY F.FILM_ID, F.RELEASE_DATE " +
                        "ORDER BY F.RELEASE_DATE;",
                new MapSqlParameterSource()
                        .addValue("DIRECTOR_ID", directorId),
                filmMapper);
    }

    @Override
    public List<Film> findFilmsDirectorByLikes(Long directorId) {
        return jdbcTemplate.query(
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
                        "JOIN FILM_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "WHERE FD.DIRECTOR_ID = :DIRECTOR_ID " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY RATE DESC;",
                new MapSqlParameterSource()
                        .addValue("DIRECTOR_ID", directorId),
                filmMapper);
    }

    @Override
    public List<Film> findRecommendedFilms(Long userId) {
        return jdbcTemplate.query(
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
                        "GROUP BY F.FILM_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId),
                FilmDbStorage.filmMapper);
    }

    private MapSqlParameterSource getFilmParams(Film film) {
        return new MapSqlParameterSource()
                .addValue("FILM_ID", film.getId())
                .addValue("FILM_NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("RATING_ID", film.getMpa().getId());
    }
}

