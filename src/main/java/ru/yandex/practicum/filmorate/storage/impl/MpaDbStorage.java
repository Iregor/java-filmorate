package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("mpaDb")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"rating_mpa\" " +
                        "ORDER BY \"rating_id\" ",
                (rs, rowNum) -> getMpaFromResultSet(rs));
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"rating_mpa\" " +
                        "WHERE \"rating_id\" = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = getMpaFromSqlRowSet(mpaRows);
            log.debug("Rating is found: {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            log.debug("Rating {} is not found.", id);
            return Optional.empty();
        }
    }

    @Override
    public Mpa create(Mpa mpa) {
        jdbcTemplate.update(
                "INSERT INTO \"rating_mpa\" (\"name\") VALUES (?)",
                mpa.getName());
        return findByName(mpa.getName());
    }

    @Override
    public Mpa update(Mpa mpa) {
        if (findById(mpa.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Rating MPA %d is not found.", mpa.getId()));
        }
        jdbcTemplate.update(
                "UPDATE \"rating_mpa\" " +
                        "SET \"name\" = ? " +
                        "WHERE \"rating_id\" = ? ",
                mpa.getName(), mpa.getId());
        return mpa;
    }

    private Mpa findByName(String name) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"rating_mpa\" " +
                        "WHERE \"name\" = ? ", name);
        if (mpaRows.next()) {
            return getMpaFromSqlRowSet(mpaRows);
        } else {
            log.debug("Data is not found.");
            return null;
        }
    }

    private Mpa getMpaFromResultSet(ResultSet rs) throws SQLException {
        return new Mpa(rs.getLong("rating_id"),
                rs.getString("name"));
    }

    private Mpa getMpaFromSqlRowSet(SqlRowSet srs) {
        return new Mpa(srs.getLong("rating_id"),
                srs.getString("name"));
    }
}
