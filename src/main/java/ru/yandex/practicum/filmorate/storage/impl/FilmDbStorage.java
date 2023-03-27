package ru.yandex.practicum.filmorate.storage.impl;

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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Collection<Film> result = jdbcTemplate.query(sql, (rs, rowNum) -> getFilmFromResultSet(rs));
        getGenresByFilms(result);
        return result;
    }

    @Override
    public Collection<Film> getPopularFilms(int size) {
        String sql = "SELECT * FROM \"films\" as f " +
                "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\"" +
                "LEFT OUTER JOIN (SELECT \"film_id\", COUNT(\"user_id\") AS count FROM \"likes\" " +
                "GROUP BY \"film_id\") AS cl ON cl.\"film_id\" = f.\"film_id\" " +
                "ORDER BY cl.count DESC " +
                "LIMIT ? ";
        Collection<Film> result = jdbcTemplate.query(sql, (rs, rowNum) -> getFilmFromResultSet(rs), size);
        getGenresByFilms(result);
        return result;
    }

    @Override
    public Optional<Film> findById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"films\" AS f " +
                        "JOIN \"rating_mpa\" AS mpa ON f.\"rating_id\" = mpa.\"rating_id\" " +
                        "WHERE \"film_id\" = ?", id);

        if (filmRows.next()) {
            Film film = getFilmFromSqlRowSet(filmRows);
            film.setGenres(getGenresByFilm(film));
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

        Long newFilmId = getFilmFromDb(film).getId();
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                            "VALUES (?, ?)",
                    newFilmId, genre.getId());
        }
        Film result = getFilmFromDb(film);
        result.setGenres(getGenresByFilm(result));
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
        film = getFilmFromDb(film);
        film.setGenres(getGenresByFilm(film));
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


    private Set<Genre> getGenresByFilm(Film film) {
        String sql = "SELECT * FROM \"film_genres\" fg " +
                "JOIN \"genres\" g on fg.\"genre_id\" = g.\"genre_id\" " +
                "WHERE \"film_id\" = ? " +
                "ORDER BY \"genre_id\" ";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getLong("genre_id"),
                        rs.getString("genre_name")), film.getId()));
    }

    private void getGenresByFilms(Collection<Film> films) {
        Map<Long, Film> mapOfFilm = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(mapOfFilm.size(), "?"));
        String sql = String.format("SELECT * FROM \"film_genres\" fg " +
                "JOIN \"genres\" g on fg.\"genre_id\" = g.\"genre_id\" " +
                "WHERE \"film_id\" IN (%s) " +
                "ORDER BY \"film_id\", \"genre_id\" ", inSql);

        jdbcTemplate.query(sql, (rs, rowNum) -> mapOfFilm
                        .get(rs.getLong("film_id"))
                        .getGenres()
                        .add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))),
                mapOfFilm.keySet().toArray());
    }
}
