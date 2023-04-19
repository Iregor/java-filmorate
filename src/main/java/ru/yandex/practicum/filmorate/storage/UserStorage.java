package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    List<User> findFriends(Long userId);

    List<User> findCommonFriends(Long userId, Long friendId);

    Optional<User> findById(Long userId);

    Optional<User> create(User user);

    Optional<User> update(User user);

    void remove(Long userId);
}
