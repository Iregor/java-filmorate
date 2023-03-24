package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDb")
    private final UserStorage userStorage;
    @Qualifier("friendDb")
    private final FriendStorage friendStorage;
    @Qualifier("likesDb")
    private final LikesStorage likesStorage;

    public Collection<User> findAll() {
        Collection<User> result = userStorage.findAll();
        result.forEach(this::makeData);
        log.info("Found {} user(s).", result.size());
        return result;
    }

    public User findById(Long userId) {
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %d is not found.", userId));
        }
        makeData(result.get());
        log.info("User {} is found.", result.get().getId());
        return result.get();
    }

    public User create(User user) {
        User result = userStorage.create(user);
        makeData(result);
        log.info("User {} {} added.", result.getId(), result.getLogin());
        return result;
    }

    public User update(User user) {
        User result = userStorage.update(user);
        makeData(result);
        log.info("User {} updated.", result.getId());
        return result;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        if (userStorage.findById(friendId).isEmpty()) {
            log.warn("Friend {} is not found.", friendId);
            throw new IncorrectObjectIdException(String.format("Friend %s is not found.", friendId));
        }
        friendStorage.addFriend(userId, friendId);
        log.info("User {} added user {} to friends.", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        if (userStorage.findById(friendId).isEmpty()) {
            log.warn("Friend {} is not found.", friendId);
            throw new IncorrectObjectIdException(String.format("Friend %s is not found.", friendId));
        }
        friendStorage.delFriend(userId, friendId);
        log.info("User {} deleted user {} from friends.", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        Collection<User> result = userStorage.findAll()
                .stream()
                .filter(user -> friendStorage
                        .getFriends(userId)
                        .contains(user.getId()))
                .collect(Collectors.toList());
        result.forEach(this::makeData);
        log.info("Found {} user(s).", result.size());
        return result;
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        Collection<User> result = userStorage.findAll()
                .stream()
                .filter(user -> friendStorage
                        .getCommonFriends(userId, friendId)
                        .contains(user.getId()))
                .collect(Collectors.toList());
        result.forEach(this::makeData);
        log.info("Found {} user(s).", result.size());
        return result;
    }

    private void makeData(User user) {
        user.setFriends(new HashSet<>(friendStorage.getFriends(user.getId())));
        user.setLikeFilms(new HashSet<>(likesStorage.getUserLikes(user.getId())));
    }
 }
