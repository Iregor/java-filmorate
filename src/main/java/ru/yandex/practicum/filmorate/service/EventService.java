package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
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

    public Collection<Event> getFeed(long userId){
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        return eventStorage.getFeed(userId);
    }

    public void addEvent(Event event) {
        eventStorage.addEvent(event);
        log.info("Добавлен ивент в ленту событий со следующими значениями: " +
                        "userId: {}, eventType: {}, operation: {}, entityId: {}", event.getUserId(), event.getEventType(),
                event.getOperation(), event.getEntityId());
    }
}
