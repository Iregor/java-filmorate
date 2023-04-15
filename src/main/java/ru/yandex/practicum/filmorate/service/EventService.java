package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dao.EventDBStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventDBStorage eventStorage;

    public Collection<Event> getFeed(long id){
        return eventStorage.getFeed(id);
    }
}
