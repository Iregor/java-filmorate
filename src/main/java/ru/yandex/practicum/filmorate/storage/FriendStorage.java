package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface FriendStorage {
    Collection<Long> getFriends(Long userId);

    void addFriend(Long userId, Long friendId);

    void delFriend(Long userId, Long friendId);

    Collection<Long> getCommonFriends(Long userId, Long friendId);

}
