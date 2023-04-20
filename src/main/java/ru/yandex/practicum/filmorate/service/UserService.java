package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LikesStorage likesStorage;
    private final EventService eventService;
    private final FilmService filmService;

    public List<User> findAll() {
        List<User> result = userStorage.findAll();
        log.info("Found {} user(s).", result.size());
        addDataUsers(result);
        return result;
    }

    public User findById(Long userId) {
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %d is not found.", userId));
        }
        log.info("User {} is found.", result.get().getId());
        addDataUsers(List.of(result.get()));
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
        addDataUsers(List.of(result.get()));
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
        addDataUsers(List.of(result.get()));
        log.info("User {} {} updated.",
                result.get().getId(), result.get().getLogin());
        return result.get();
    }

    public void delete(Long userId) {
        Optional<User> result = userStorage.findById(userId);
        if (result.isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        userStorage.remove(userId);
        log.info("User {} removed.", result.get().getLogin());
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
        addFeed(userId, friendId, Operation.ADD);
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
        addFeed(userId, friendId, Operation.REMOVE);
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        List<User> result = userStorage.findFriends(userId);
        log.info("Found {} friend(s).", result.size());
        addDataUsers(result);
        return result;
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        if (userStorage.findById(friendId).isEmpty()) {
            log.warn("Friend {} is not found.", friendId);
            throw new IncorrectObjectIdException(String.format("Friend %s is not found.", friendId));
        }
        List<User> result = userStorage.findCommonFriends(userId, friendId);
        log.info("Found {} friend(s).", result.size());
        addDataUsers(result);
        return result;
    }

    public List<Film> getRecommendedFilms(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        return filmService.getRecommendedFilms(userId);
    }

    private void addFeed(Long userId, Long friendId, Operation operation) {
        eventService.addEvent(userId, friendId, EventType.FRIEND, operation);
    }

    private void addDataUsers(List<User> users) {
        Map<Long, User> usersMap = users
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Set<Long>> friendsMap = friendStorage.findByUsers(usersMap.keySet());
        Map<Long, Set<Long>> likesMap = likesStorage.findByUsers(usersMap.keySet());
        users.forEach(user -> {
            user.setFriends(new HashSet<>());
            user.setLikeFilms(new HashSet<>());
            if (Objects.requireNonNull(friendsMap).containsKey(user.getId())) {
                user.setFriends(friendsMap.get(user.getId()));
            }
            if (Objects.requireNonNull(likesMap).containsKey(user.getId())) {
                user.setLikeFilms(likesMap.get(user.getId()));
            }
        });
    }
}
