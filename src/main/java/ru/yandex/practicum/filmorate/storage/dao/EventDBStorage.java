package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("EventDBStorage")
@Slf4j
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public EventDBStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    static final RowMapper<Event> eventMapper = (rs, rowNum) -> Event.builder()
            .eventId(rs.getInt("EVENT_ID"))
            .userId(rs.getLong("USER_ID"))
            .timestamp(Timestamp.valueOf(rs.getTimestamp("TIMESTAMP").toLocalDateTime()))
            .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
            .operation(Operation.valueOf(rs.getString("OPERATION")))
            .entityId(rs.getLong("ENTITY_ID"))
            .build();

    @Override
    public Collection<Event> getFeed(Long id) {
        String query = "SELECT * FROM FEEDS WHERE USER_ID = ?";
        return jdbcTemplate.query(query, new Object[]{id}, eventMapper);
    }

//    public void createEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
//        log.info("Добавление события в ленту пользователя с с id-{} ", userId);
//        String sql = "INSERT INTO FEEDS (" +
//                "TIMESTAMP" +
//                ", USER_ID" +
//                ", ENTITY_ID" +
//                ", EVENT_TYPE" +
//                ", OPERATION" +
//                ") " +
//                "VALUES (?,?,?,?,?)";
//
//        jdbcTemplate.update(connection -> {
//            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            stmt.setTimestamp(1, timestamp);
//            stmt.setInt(2, Math.toIntExact(userId));
//            stmt.setInt(3, Math.toIntExact(entityId));
//            stmt.setString(4, eventType.toString());
//            stmt.setString(5, operation.toString());
//            return stmt;
//        });
//    }

    @Override
    public void addEvent(Event event) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("USER_ID", event.getUserId())
                .addValue("EVENT_TYPE", event.getEventType().name())
                .addValue("OPERATION", event.getOperation())
                .addValue("ENTITY_ID", event.getEntityId())
                .addValue("TIMESTAMP", Timestamp.valueOf(LocalDateTime.now()));
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("FEEDS")
                .usingGeneratedKeyColumns("EVENT_ID");
        insert.execute(param);
    }

}
