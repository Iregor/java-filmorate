package ru.yandex.practicum.filmorate.storage.impl.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("filmDb")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM \"films\" AS f " +
                "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\" " +
                "ORDER BY \"film_id\" ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmFromResultSet(rs));
    }

    @Override
    public Collection<Film> getPopularFilms(int size) {
        String sql = "SELECT * FROM \"films\" as f " +
                "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\"" +
                "LEFT OUTER JOIN (SELECT \"film_id\", COUNT(\"user_id\") AS count FROM \"likes\" " +
                "GROUP BY \"film_id\") AS cl ON cl.\"film_id\" = f.\"film_id\" " +
                "ORDER BY cl.count DESC " +
                "LIMIT ? ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmFromResultSet(rs), size);
    }

    @Override
    public Optional<Film> findById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"films\" AS f " +
                        "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\"" +
                        "WHERE \"film_id\" = ?", id);

        if (filmRows.next()) {
            Film film = getFilmFromSqlRowSet(filmRows);
            log.debug("Film found: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.debug("Film {} is not found.", id);
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        jdbcTemplate.update(
                "INSERT INTO \"films\" (\"rating_id\", \"film_name\", \"description\", " +
                        "\"release_date\", \"length\", \"rate\")" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                film.getMpa().getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate());

        Film result = getFilmFromDb(film);

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                            "VALUES (?, ?)",
                    result.getId(), genre.getId());
        }

        return result;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                "UPDATE \"films\" " +
                        "SET \"film_name\" = ?, \"description\" = ?, \"release_date\" = ?, \"length\" = ? ," +
                        " \"rate\" = ?, \"rating_id\" = ? " +
                        "WHERE \"film_id\" = ? ",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(),
                film.getMpa().getId(), film.getId());

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                            "VALUES (?, ?)",
                    film.getId(), genre.getId());
        }

        return film;
    }


    @Override
    public void deleteFilmGenres(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "DELETE FROM \"film_genres\" WHERE \"film_id\" = ? AND \"genre_id\" = ? ",
                filmId, genreId);
    }


    @Override
    public Collection<Long> getFilmLikes(Long filmId) {
        return jdbcTemplate.query(
                "SELECT * FROM \"likes\" " +
                        "WHERE \"film_id\" = ?",
                (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private Film getFilmFromDb(Film film) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"films\" as f " +
                        "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\" " +
                        "WHERE \"film_name\" = ? AND \"release_date\" = ?",
                film.getName(), film.getReleaseDate());
        if (filmRows.next()) {
            return getFilmFromSqlRowSet(filmRows);
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }

    private Film getFilmFromResultSet(ResultSet rs) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getString("release_date"),
                rs.getInt("length"),
                rs.getInt("rate"),
                new Mpa(rs.getLong("rating_id"),
                        rs.getString("rating_name")));
    }

    private Film getFilmFromSqlRowSet(SqlRowSet srs) {
        return new Film(
                srs.getLong("film_id"),
                srs.getString("film_name"),
                srs.getString("description"),
                srs.getString("release_date"),
                srs.getInt("length"),
                srs.getInt("rate"),
                new Mpa(srs.getLong("rating_id"),
                        srs.getString("rating_name")));
    }
}
