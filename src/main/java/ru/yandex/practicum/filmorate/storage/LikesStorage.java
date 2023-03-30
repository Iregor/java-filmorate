package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    void add(Long filmId, Long userId);

    void remove(Long filmId, Long userId);
}
