package ru.yandex.practicum.filmorate.storage;

public interface FriendStorage {
    void addFriend(Long userId, Long friendId);

    void delFriend(Long userId, Long friendId);
}
