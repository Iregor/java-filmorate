package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("EventDBStorage")
@Slf4j
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    static final RowMapper<Event> eventMapper = (rs, rowNum) -> Event.builder()
            .eventId(rs.getInt("EVENT_ID"))
            .userId(rs.getInt("USER_ID"))
            .timestamp(Timestamp.valueOf(rs.getTimestamp("TIMESTAMP").toLocalDateTime()))
            .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
            .operation(Operation.valueOf(rs.getString("OPERATION")))
            .entityId(rs.getInt("ENTITY_ID"))
            .build();


    @Override
    public Collection<Event> getFeed(Long id) {
        String query = "SELECT * FROM FEEDS WHERE USER_ID = ?";
        return jdbcTemplate.query(query, new Object[]{id}, eventMapper);
    }

    public void createEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        log.info("Добавление события в ленту пользователя с с id-{} ", userId);
        String sql = "INSERT INTO FEEDS (" +
                "TIMESTAMP" +
                ", USER_ID" +
                ", ENTITY_ID" +
                ", EVENT_TYPE" +
                ", OPERATION" +
                ") " +
                "VALUES (?,?,?,?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(1, timestamp);
            stmt.setInt(2, Math.toIntExact(userId));
            stmt.setInt(3, Math.toIntExact(entityId));
            stmt.setString(4, eventType.toString());
            stmt.setString(5, operation.toString());
            return stmt;
        });
    }

    private Optional<Event> findById(long id) {
        try {
            String query = "SELECT * FROM FEEDS WHERE EVENT_ID = :EVENT_ID;";
            SqlParameterSource params = new MapSqlParameterSource("EVENT_ID", id);
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, params.getParameterNames(), eventMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Map<String, Object> getFeedParams(Event event) {
        Map<String, Object> params = new HashMap<>();
        params.put("TIMESTAMP", event.getTimestamp());
        params.put("USER_ID", event.getUserId());
        params.put("EVENT_TYPE", event.getEventType().name());
        params.put("OPERATION", event.getOperation().name());
        params.put("ENTITY_ID", event.getEntityId());
        return params;
    }
}
