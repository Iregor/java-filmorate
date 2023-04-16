package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Component
public interface EventStorage {
    Collection<Event> getFeed(Long eventId);

    void addEvent(Event event);
}
