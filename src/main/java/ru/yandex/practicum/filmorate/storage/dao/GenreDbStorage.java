package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import javax.sql.DataSource;
import java.util.*;

@Repository("genreDb")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    static final RowMapper<Genre> genreMapper = (rs, rowNum) -> new Genre(
            rs.getLong("GENRE_ID"),
            rs.getString("GENRE_NAME"));

    static final ResultSetExtractor<Map<Long, Set<Genre>>> genresExtractor = rs -> {
        Map<Long,Set<Genre>> filmGenres = new HashMap<>();
        while (rs.next()) {
            filmGenres.putIfAbsent(rs.getLong("FILM_ID"), new HashSet<>());
            filmGenres.get(rs.getLong("FILM_ID")).add(new Genre(
                    rs.getLong("GENRE_ID"),
                    rs.getString("GENRE_NAME")));
        }
        return filmGenres;
    };

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM GENRES ORDER BY GENRE_ID;",
                genreMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.
                    queryForObject("SELECT * FROM GENRES WHERE GENRE_ID = :GENRE_ID;",
                            new MapSqlParameterSource()
                                    .addValue("GENRE_ID", id),
                            genreMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Genre> create(Genre genre) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("GENRES")
                .usingGeneratedKeyColumns("GENRE_ID");
        long id = insert
                .executeAndReturnKey(getGenreParams(genre))
                .longValue();
        return findById(id);
    }

    @Override
    public Optional<Genre> update(Genre genre) {
        String sql = "UPDATE GENRES " +
                "SET GENRE_NAME = :GENRE_NAME " +
                "WHERE GENRE_ID = :GENRE_ID;";
        jdbcTemplate.update(sql, getGenreParams(genre));
        return findById(genre.getId());
    }

    private SqlParameterSource getGenreParams(Genre genre) {
        return new MapSqlParameterSource()
                .addValue("GENRE_ID", genre.getId())
                .addValue("GENRE_NAME", genre.getName());
    }
}
