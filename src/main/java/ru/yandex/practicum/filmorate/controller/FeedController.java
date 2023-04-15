package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FeedController {
    private final EventService eventService;

    @GetMapping("/{id}/feed")
    public Collection<Event> getFeed(@PathVariable Long id) {
        Collection<Event> events = eventService.getFeed(id);
        log.info("Запрошена лента событий пользователя с id {}: {}", id, events);
        return events;
    }
}
