package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("genreDb")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "select * from \"genres\"order by \"genre_id\" ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> findById(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "select * from \"genres\" where \"genre_id\" = ?", id);
        if(genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
            log.info("Найден рейтинг: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Genre create(Genre genre) {
        jdbcTemplate.update(
                "INSERT INTO \"genres\" (\"name\") VALUES (?)",
                genre.getName());
        return getGenreFromDb(genre.getName());
    }

    @Override
    public Genre update(Genre genre) {
        if(findById(genre.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Genre %d is not found.", genre.getId()));
        }
        jdbcTemplate.update(
                "UPDATE \"genres\" " +
                        "SET \"name\" = ? " +
                        "WHERE \"genre_id\" = ? ",
                genre.getName(), genre.getId());
        return genre;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Long id = rs.getLong("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Genre getGenreFromDb(String name) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"genres\" WHERE \"name\" = ? ", name);
        if(genreRows.next()) {
            return new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
        } else {
            log.info("Данные не найдены.");
            return null;
        }
    }
}
