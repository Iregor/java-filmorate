package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Collection<User> findFriends(Long userId);

    Collection<User> findCommonFriends(Long userId, Long friendId);

    Optional<User> findById(Long userId);

    Optional<User> create(User user);

    Optional<User> update(User user);
}
