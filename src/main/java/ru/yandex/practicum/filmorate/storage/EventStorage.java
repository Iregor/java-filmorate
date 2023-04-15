package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

@Component
public interface EventStorage {
    Collection<Event> getFeed(Long id);
    void createEvent(Long userId, Long entityId, EventType eventType, Operation operation);
}
