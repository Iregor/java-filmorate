package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long userId) {
        return userStorage.findById(userId);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Map<String, Long> addFriend(Long userId, Long friendId) {
        Map<String, Long> result = validateUserDataRequest(userId, friendId);
        if (!result.isEmpty()) {
            return result;
        }
        userStorage.findById(userId).getFriends().add(friendId);
        userStorage.findById(friendId).getFriends().add(userId);
        return null;
    }

    public Map<String, Long> delFriend(Long userId, Long friendId) {
        Map<String, Long> result = validateUserDataRequest(userId, friendId);
        if (!result.isEmpty()) {
            return result;
        }
        userStorage.findById(userId).getFriends().remove(friendId);
        userStorage.findById(friendId).getFriends().remove(userId);
        return null;
    }

    private Map<String, Long> validateUserDataRequest(Long userId, Long friendId) {
        Map<String, Long> result = new HashMap<>();
        if (userStorage.findById(userId) == null) {
            result.put("userId", userId);
        }
        if (userStorage.findById(friendId) == null) {
            result.put("friendId", friendId);
        }
        return result;
    }


    public Collection<User> getFriends(Long userId) {
        return userStorage
                .findAll()
                .stream()
                .filter(user -> userStorage.findById(userId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        Set<Long> commonFriendId = userStorage
                .findById(userId)
                .getFriends()
                .stream()
                .filter(userStorage.findById(friendId).getFriends()::contains)
                .collect(Collectors.toSet());

        return userStorage
                .findAll()
                .stream()
                .filter(user -> commonFriendId.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
