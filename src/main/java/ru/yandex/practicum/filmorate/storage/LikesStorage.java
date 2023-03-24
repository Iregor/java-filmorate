package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    void like(Long filmId, Long userId);

    void dislike(Long filmId, Long userId);
}
