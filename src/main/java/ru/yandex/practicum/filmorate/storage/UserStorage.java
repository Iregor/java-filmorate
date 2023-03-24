package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long friendId);

    Optional<User> findById(Long id);

    User create(User user);

    User update(User user);

    Collection<Long> getUserLikes(Long userId);
}
