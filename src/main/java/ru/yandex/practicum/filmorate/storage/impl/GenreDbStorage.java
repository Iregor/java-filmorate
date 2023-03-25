package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
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

    @Override
    public Collection<Genre> readAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"genres\" " +
                        "ORDER BY \"genre_id\" ",
                (rs, rowNum) -> getGenreFromResultSet(rs));
    }

    @Override
    public Collection<Genre> readRowByFilmId(Long filmId) {
        Collection<Genre> result = jdbcTemplate.query(
                "SELECT \"film_genres\".\"genre_id\", \"genre_name\"\n" +
                        "FROM \"film_genres\"\n" +
                        "JOIN \"genres\" AS g ON g.\"genre_id\" = \"film_genres\".\"genre_id\"\n" +
                        "WHERE \"film_id\" = ?" +
                        "ORDER BY \"genre_id\" ",
                (rs, rowNum) -> getGenreFromResultSet(rs), filmId);
        log.info("Found {} genre(s).", result.size());
        return result;
    }

    @Override
    public Optional<Genre> readById(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"genres\" " +
                        "WHERE \"genre_id\" = ?", id);
        if(genreRows.next()) {
            Genre genre = getGenreFromSqlRowSet(genreRows);
            log.debug("Genre found: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.debug("Genre {} is not found.", id);
            return Optional.empty();
        }
    }

    @Override
    public Genre writeRow(Genre genre) {
        jdbcTemplate.update(
                "INSERT INTO \"genres\" (\"genre_name\") VALUES (?)",
                genre.getName());
        return findByName(genre.getName());
    }

    @Override
    public Genre updateRow(Genre genre) {
        jdbcTemplate.update(
                "UPDATE \"genres\" " +
                        "SET \"genre_name\" = ? " +
                        "WHERE \"genre_id\" = ? ",
                genre.getName(), genre.getId());
        return genre;
    }

    private Genre findByName(String name) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"genres\" " +
                        "WHERE \"genre_name\" = ? ", name);
        if(genreRows.next()) {
            return getGenreFromSqlRowSet(genreRows);
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }

    private Genre getGenreFromResultSet(ResultSet rs) throws SQLException {
        return new Genre(rs.getLong("genre_id"),
                rs.getString("genre_name"));
    }

    private Genre getGenreFromSqlRowSet(SqlRowSet srs) {
        return new Genre(srs.getLong("genre_id"),
                srs.getString("genre_name"));
    }
}
