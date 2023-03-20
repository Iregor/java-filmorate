package ru.yandex.practicum.filmorate.storage.impl.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
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
        String sql = "SELECT * FROM \"films\" ORDER BY \"film_id\" ";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Film(rs.getLong("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("release_date"),
                        rs.getInt("length"),
                        rs.getInt("rate"),
                        rs.getInt("rating_id")
                ));
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
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) { // добавить получение объекта из базы и создать класс
        jdbcTemplate.update(
                "INSERT INTO \"films\" (\"rating_id\", \"name\", \"description\", \"release_date\", \"length\", \"rate\")" +
                        "VALUES (?,?,?,?,?,?)",
                film.getMpa().getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate());
        return getFilmFromDb(film);
    }

    @Override
    public Film update(Film film) {
        return null;
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
            log.info("Данные не найдены.");
            return null;
        }
    }
}
