package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDb")
    private final UserStorage userStorage;
    @Qualifier("friendDb")
    private final FriendStorage friendStorage;

    public Collection<User> findAll() {
        Collection<User> result = userStorage.findAll();
        log.info("Found {} user(s).", result.size());
        return result;
    }

    public User findById(Long userId) {
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %d is not found.", userId));
        }
        log.info("User {} is found.", result.get().getId());
        return result.get();
    }

    public User create(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Optional<User> result = userStorage.create(user);
        if (result.isEmpty()) {
            log.warn("User {} is not created.",
                    user.getLogin());
            throw new IncorrectObjectIdException(String.format("User %s is not created.",
                    user.getLogin()));
        }
        log.info("User {} {} created.",
                result.get().getId(), result.get().getLogin());
        return result.get();
    }

    public User update(User user) {
        Optional<User> result = userStorage.update(user);
        if (result.isEmpty()) {
            log.warn("User {} {} is not updated.",
                    user.getId(), user.getLogin());
            throw new IncorrectObjectIdException(String.format("User %d %s is not updated.",
                    user.getId(), user.getLogin()));
        }
        log.info("User {} {} updated.",
                result.get().getId(), result.get().getLogin());
        return result.get();
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
        friendStorage.add(userId, friendId);
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
        friendStorage.remove(userId, friendId);
        log.info("User {} deleted user {} from friends.", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        return userStorage.findFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.findCommonFriends(userId, friendId);
    }
}
