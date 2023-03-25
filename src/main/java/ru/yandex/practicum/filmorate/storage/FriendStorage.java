package ru.yandex.practicum.filmorate.storage;

public interface FriendStorage {
    void writeRow(Long userId, Long friendId);

    void deleteRow(Long userId, Long friendId);
}
