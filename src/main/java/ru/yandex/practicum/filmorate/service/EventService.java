package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.EventDBStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventDBStorage eventStorage;
    private final UserStorage userStorage;

    public Collection<Event> getFeed(Long userId) {
        log.info("Requested event feed of a user with id {}", userId);
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        return eventStorage.getFeed(userId);
    }

    public void addEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build());
        log.info("Added an event to the feed: " +
                        "userId: {}, eventType: {}, operation: {}, entityId: {}", userId, eventType,
                operation, entityId);
    }
}
