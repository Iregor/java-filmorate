package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Collection<User> findFriends(Long userId);

    Collection<User> findCommonFriends(Long userId, Long friendId);

    Optional<User> findById(Long userId);

    Optional<User> create(User user);

    Optional<User> update(User user);

    List<Integer> convertMaxCommonLikes(Integer id);

    Map<Integer, List<Integer>> getDiffFilms(Integer id);

    Map<Integer, Integer> getFilmsScore(Integer id);

    void remove(Long userId);
}
