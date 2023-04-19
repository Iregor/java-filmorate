package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Component
public interface EventStorage {
    List<Event> getFeed(Long userId);

    void addEvent(Event event);
}
