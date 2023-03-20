package ru.yandex.practicum.filmorate.storage.impl;

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
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sql = "select * from \"rating_mpa\"order by \"rating_id\" ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "select * from \"rating_mpa\" where \"rating_id\" = ?", id);
        if(mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getLong("rating_id"),
                    mpaRows.getString("name"));
            log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Mpa create(Mpa mpa) {
        jdbcTemplate.update(
                "INSERT INTO \"rating_mpa\" (\"name\") VALUES (?)",
                mpa.getName());
        return getMpaFromDb(mpa.getName());
    }

    @Override
    public Mpa update(Mpa mpa) {
        if(findById(mpa.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Rating MPA %d is not found.", mpa.getId()));
        }
        jdbcTemplate.update(
                "UPDATE \"rating_mpa\" " +
                        "SET \"name\" = ? " +
                        "WHERE \"rating_id\" = ? ",
                mpa.getName(), mpa.getId());
        return mpa;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Long id = rs.getLong("rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }

    private Mpa getMpaFromDb(String name) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"rating_mpa\" WHERE \"name\" = ? ", name);
        if(mpaRows.next()) {
            return new Mpa(
                    mpaRows.getLong("genre_id"),
                    mpaRows.getString("name"));
        } else {
            log.info("Данные не найдены.");
            return null;
        }
    }
}
