package ru.yandex.practicum.filmorate.storage.impl.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("filmDb")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll(String name, LocalDate after, LocalDate before) {
        if (after == null) {
            after = LocalDate.parse("1895-12-28");
        }
        if (before == null) {
            before = LocalDate.now();
        }

        String sql = "SELECT * FROM \"films\" " +
                "WHERE \"release_date\" BETWEEN ? AND ? " +
                "ORDER BY \"film_id\" ";

        if (name != null) {
            sql = "SELECT * FROM \"films\" " +
                    "WHERE \"release_date\" BETWEEN ? AND ? " +
                    "AND \"name\" = ? " +
                    "ORDER BY \"film_id\" ";
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new Film(rs.getLong("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("release_date"),
                            rs.getInt("length"),
                            rs.getInt("rate"),
                            rs.getInt("rating_id")
                    ), after, before, name);
        }

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Film(rs.getLong("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("release_date"),
                        rs.getInt("length"),
                        rs.getInt("rate"),
                        rs.getInt("rating_id")
                ), after, before);
    }

    @Override
    public Optional<Film> findById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"films\" WHERE \"film_id\" = ?", id);

        if(filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getString("release_date"),
                    filmRows.getInt("length"),
                    filmRows.getInt("rate"),
                    filmRows.getInt("rating_id")
            );
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
                "INSERT INTO \"films\" (\"rating_id\", \"name\", \"description\", " +
                        "\"release_date\", \"length\", \"rate\")" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                film.getMpa().getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate());

        Film result = getFilmFromDb(film);

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                            "VALUES (?, ?)" ,
                    result.getId(), genre.getId());
        }

        return result;
    }

    @Override
    public Film update(Film film) {
        if(findById(film.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", film.getId()));
        }

        jdbcTemplate.update(
                "UPDATE \"films\" " +
                        "SET \"name\" = ?, \"description\" = ?, \"release_date\" = ?, \"length\" = ? ," +
                        " \"rate\" = ?, \"rating_id\" = ? " +
                        "WHERE \"film_id\" = ? ",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(),
                film.getMpa().getId(), film.getId());

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                            "VALUES (?, ?)" ,
                    film.getId(), genre.getId());
        }

        return film;
    }

    private Film getFilmFromDb(Film film) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"films\" WHERE \"name\" = ? AND \"release_date\" = ?",
                film.getName(), film.getReleaseDate());
        if(filmRows.next()) {
            return new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getString("release_date"),
                    filmRows.getInt("length"),
                    filmRows.getInt("rate"),
                    filmRows.getInt("rating_id"));
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }
}
