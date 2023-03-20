package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LikesStorage likesStorage;

    @Autowired
    public UserService(@Qualifier("userDb") UserStorage userStorage,
                       @Qualifier("friendDb") FriendStorage friendStorage,
                       @Qualifier("likesDb") LikesStorage likesStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.likesStorage = likesStorage;
    }

    public Collection<User> findAll() { // вынести оснастку в другой класс
        Collection<User> result = userStorage.findAll();
        result.forEach(this::makeData);
        return result;
    }

    public Optional<User> findById(Long userId) { // вынести оснастку в другой класс
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            return result;
        }
        makeData(result.get());
        return result;
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
        friendStorage.addFriend(userId, friendId);
        return null;
    }

    public Map<String, Long> delFriend(Long userId, Long friendId) {
        Map<String, Long> result = validateUserDataRequest(userId, friendId);
        if (!result.isEmpty()) {
            return result;
        }
        friendStorage.delFriend(userId, friendId);
        return null;
    }

    public Collection<User> getFriends(Long userId) {
        return userStorage.findAll()
                .stream()
                .filter(user -> friendStorage
                        .getFriends(userId)
                        .contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.findAll()
                .stream()
                .filter(user -> friendStorage
                        .getCommonFriends(userId, friendId)
                        .contains(user.getId()))
                .collect(Collectors.toList());
    }

    private Map<String, Long> validateUserDataRequest(Long userId, Long friendId) {
        Map<String, Long> result = new HashMap<>();
        if (userStorage.findById(userId).isEmpty()) {
            result.put("userId", userId);
        }
        if (userStorage.findById(friendId).isEmpty()) {
            result.put("friendId", friendId);
        }
        return result;
    }

    private void makeData(User user) {
        user.setFriends(new HashSet<>(friendStorage.getFriends(user.getId())));
        user.setLikeFilms(new HashSet<>(likesStorage.getUserLikes(user.getId())));
    }
 }
