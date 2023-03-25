package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    void writeRow(Long filmId, Long userId);

    void deleteRow(Long filmId, Long userId);
}
