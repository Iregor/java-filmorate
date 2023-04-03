package ru.yandex.practicum.filmorate.storage;

import java.util.Map;
import java.util.Set;

public interface LikesStorage {

    Map<Long, Set<Long>> findByFilms(Set<Long> filmIds);

    Map<Long, Set<Long>> findByUsers(Set<Long> userIds);

    void add(Long filmId, Long userId);

    void remove(Long filmId, Long userId);
}
