package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository("mpaDb")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> readAll() {
        return jdbcTemplate.query(
                "SELECT * FROM RATING ORDER BY RATING_ID ", new MpaMapper());
    }

    @Override
    public Optional<Mpa> readById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.
                    queryForObject("SELECT * FROM RATING WHERE RATING_ID = :ID;",
                            Map.of("ID", id),
                            new MpaMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class MpaMapper implements RowMapper<Mpa> {

        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Mpa(rs.getLong("RATING_ID"),
                    rs.getString("RATING_NAME"));
        }
    }
}
