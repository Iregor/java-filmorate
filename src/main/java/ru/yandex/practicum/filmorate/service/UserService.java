package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.findById(userId).getFriends().add(friendId);
        userStorage.findById(friendId).getFriends().add(userId);
    }

    public void delFriend(Long userId, Long friendId) {
        userStorage.findById(userId).getFriends().remove(friendId);
        userStorage.findById(friendId).getFriends().remove(userId);
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
