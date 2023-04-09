package ru.yandex.practicum.filmorate.storage;

import java.util.Map;
import java.util.Set;

public interface FriendStorage {

    Map<Long, Set<Long>> findByUsers(Set<Long> userIds);

    void add(Long userId, Long friendId);

    void remove(Long userId, Long friendId);
}
