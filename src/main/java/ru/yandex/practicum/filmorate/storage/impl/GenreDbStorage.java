package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

/*    public GenreDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        jdbcTemplate.update("INSERT INTO \"genres\" (\"name\") " +
                "VALUES ('Комедия'), " +
                "('Драма')," +
                "('Мультфильм')," +
                "('Триллер')," +
                "('Документальный'),"+
                "('Боевик')");
    }*/

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"genres\" " +
                        "ORDER BY \"genre_id\" ",
                (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Collection<Genre> findAllByFilmId(Long filmId) {
        Collection<Genre> result = jdbcTemplate.query(
                "SELECT \"film_genres\".\"genre_id\", \"name\"\n" +
                        "FROM \"film_genres\"\n" +
                        "JOIN \"genres\" AS g ON g.\"genre_id\" = \"film_genres\".\"genre_id\"\n" +
                        "WHERE \"film_id\" = ?",
                (rs, rowNum) -> makeGenre(rs), filmId);
        log.info("Found {} genre(s).", result.size());
        return result;
    }

    @Override
    public Optional<Genre> findById(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"genres\" " +
                        "WHERE \"genre_id\" = ?", id);
        if(genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
            log.debug("Genre found: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.debug("Genre {} is not found.", id);
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

    @Override
    public void delFilmGenre(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "DELETE FROM \"film_genres\" WHERE \"film_id\" = ? AND \"genre_id\" = ? ",
                filmId, genreId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Long id = rs.getLong("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Genre getGenreFromDb(String name) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"genres\" " +
                        "WHERE \"name\" = ? ", name);
        if(genreRows.next()) {
            return new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }
}
