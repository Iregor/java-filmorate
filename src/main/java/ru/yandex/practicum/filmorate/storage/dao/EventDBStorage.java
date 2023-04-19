package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;


@Component("EventDB")
@Slf4j
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;


    static final RowMapper<Event> eventMapper = (rs, rowNum) -> Event.builder()
            .eventId(rs.getLong("EVENT_ID"))
            .userId(rs.getLong("USER_ID"))
            .timestamp(rs.getLong("TIMESTAMP"))
            .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
            .operation(Operation.valueOf(rs.getString("OPERATION")))
            .entityId(rs.getLong("ENTITY_ID"))
            .build();

    @Override
    public List<Event> getFeed(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM FEEDS " +
                        "WHERE USER_ID = :USER_ID;",
                new MapSqlParameterSource()
                        .addValue("USER_ID", userId),
                eventMapper);
    }

    @Override
    public void addEvent(Event event) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("USER_ID", event.getUserId())
                .addValue("EVENT_TYPE", event.getEventType().name())
                .addValue("OPERATION", event.getOperation())
                .addValue("ENTITY_ID", event.getEntityId())
                .addValue("TIMESTAMP", Instant.now().toEpochMilli());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("FEEDS")
                .usingGeneratedKeyColumns("EVENT_ID");
        insert.execute(param);
    }
}
