package ru.yandex.practicum.filmorate.storage;

public interface FriendStorage {
    void add(Long userId, Long friendId);

    void remove(Long userId, Long friendId);
}
