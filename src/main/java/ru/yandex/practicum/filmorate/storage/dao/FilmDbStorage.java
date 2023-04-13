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
import java.util.Collection;
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
    public Collection<Film> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "ORDER BY F.FILM_ID; ",
                filmMapper);
    }

    @Override
    public Collection<Film> findPopularFilms(int size) {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "ORDER BY R.RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size),
                filmMapper);
    }

    @Override
    public Collection<Film> searchFilms(String subString, List<String> by) {
        Collection<Film> films = null;
        if (by.contains("director") && by.contains("title")) {
            films = jdbcTemplate.query(
                    "SELECT * FROM FILMS F\n" +
                            "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID \n" +
                            "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES \n" +
                            "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID \n" +
                            "LEFT JOIN \n" +
                            "(FILM_DIRECTORS FD INNER JOIN DIRECTORS D ON FD. DIRECTOR_ID  = D. DIRECTOR_ID)\n" +
                            "ON F.FILM_ID = FD.FILM_ID \n" +
                            "WHERE LOWER(F.FILM_NAME) LIKE lower(:SUBSTRING)\n" +
                            "OR LOWER(D.DIRECTOR_NAME) LIKE lower(:SUBSTRING)\n" +
                            "ORDER BY R.RATE DESC",
                    new MapSqlParameterSource()
                            .addValue("SUBSTRING", "%" + subString + "%"),
                    filmMapper);
        } else if (by.contains("title")) {
            films = jdbcTemplate.query(
                    " SELECT * FROM FILMS F\n" +
                            "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID \n" +
                            "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES \n" +
                            "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID \n" +
                            "WHERE  LOWER( F.FILM_NAME) LIKE lower(:SUBSTRING) \n" +
                            "ORDER BY R.RATE DESC",
                    new MapSqlParameterSource()
                            .addValue("SUBSTRING", "%" + subString + "%"),
                    filmMapper);
        } else if (by.contains("director")) {
            films = jdbcTemplate.query(
                    "SELECT * FROM FILMS F\n" +
                            "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID \n" +
                            "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES \n" +
                            "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID \n" +
                            "LEFT JOIN \n" +
                            "(FILM_DIRECTORS FD INNER JOIN DIRECTORS D ON FD. DIRECTOR_ID  = D. DIRECTOR_ID)\n" +
                            "ON F.FILM_ID = FD.FILM_ID \n" +
                            "WHERE LOWER(D.DIRECTOR_NAME) LIKE lower(:SUBSTRING)\n" +
                            "ORDER BY R.RATE DESC",
                    new MapSqlParameterSource()
                            .addValue("SUBSTRING", "%" + subString + "%"),
                    filmMapper);
        }
        return films;
    }

    @Override
    public Collection<Film> findPopularFilmsByGenreId(int size, Long genreId) {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE F.FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = :GENRE_ID) " +
                        "ORDER BY R.RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("GENRE_ID", genreId),
                filmMapper);
    }

    @Override
    public Collection<Film> findPopularFilmsByYear(int size, String year) {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = :YEAR " +
                        "ORDER BY R.RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("YEAR", year),
                filmMapper);
    }

    @Override
    public Collection<Film> findPopularFilmsByGenreIdAndYear(int size, Long genreId, String year) {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE F.FILM_ID IN (SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = :GENRE_ID) " +
                        "AND EXTRACT(YEAR FROM F.RELEASE_DATE) = :YEAR " +
                        "ORDER BY R.RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size)
                        .addValue("GENRE_ID", genreId)
                        .addValue("YEAR", year),
                filmMapper);
    }

    @Override
    public Collection<Film> findCommonFilms(Long userId, Long friendId) {
        return jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE F.FILM_ID IN (SELECT FILM_ID FROM LIKES WHERE USER_ID = :USER_ID) " +
                        "AND F.FILM_ID IN (SELECT FILM_ID FROM LIKES WHERE USER_ID = :FRIEND_ID) " +
                        "ORDER BY R.RATE DESC",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId)
                        .addValue("FRIEND_ID", friendId),
                filmMapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM FILMS F " +
                            "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                            "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                            "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                            "WHERE F.FILM_ID = :FILM_ID;",
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
                        "SET FILM_NAME = :FILM_NAME, DESCRIPTION = :DESCRIPTION," +
                        "RELEASE_DATE = :RELEASE_DATE, DURATION = :DURATION, " +
                        "RATING_ID = :RATING_ID " +
                        "WHERE FILM_ID = :FILM_ID; ",
                getFilmParams(film));
        return findById(film.getId());
    }

    @Override
    public void remove(Long filmId) {
        jdbcTemplate.update(
                "DELETE FROM films WHERE film_id = :FILM_ID",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId));
    }

    @Override
    public Collection<Film> findFilmsDirectorByYear(Long directorId) {
        return jdbcTemplate.query("SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE F.FILM_ID IN ( " +
                        "SELECT FILM_ID FROM FILM_DIRECTORS FD WHERE DIRECTOR_ID = :DIRECTOR_ID) " +
                        "ORDER BY F.RELEASE_DATE;",
                new MapSqlParameterSource()
                        .addValue("DIRECTOR_ID", directorId),
                filmMapper);
    }

    @Override
    public Collection<Film> findFilmsDirectorByLikes(Long directorId) {
        return jdbcTemplate.query("SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "JOIN FILM_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "WHERE FD.DIRECTOR_ID = :DIRECTOR_ID " +
                        "ORDER BY R.RATE DESC;",
                new MapSqlParameterSource()
                        .addValue("DIRECTOR_ID", directorId),
                filmMapper);
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

