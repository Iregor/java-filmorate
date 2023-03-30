package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("mpaDb")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    static final RowMapper<Mpa> mpaMapper = (rs, rowNum) -> new Mpa(
            rs.getLong("RATING_ID"),
            rs.getString("RATING_NAME"));

    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM RATING ORDER BY RATING_ID;",
                mpaMapper);
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.
                    queryForObject("SELECT * FROM RATING WHERE RATING_ID = :RATING_ID;",
                            new MapSqlParameterSource()
                                    .addValue("RATING_ID", id),
                            mpaMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
