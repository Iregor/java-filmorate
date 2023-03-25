package ru.yandex.practicum.filmorate.storage.impl.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component("userInMemory")
public class UserInMemoryStorage implements UserStorage {
    private static long userId = 1;
    private final HashMap<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return null;
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return null;
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }

    @Override
    public User create(User user) {
        UserValidator.validate(user);
        for (User userFromBase : users.values()) {
            if (userFromBase.getEmail().equals(user.getEmail())) {
                log.debug("The user with this email is already registered.");
                return null;
            }
            if (userFromBase.getLogin().equals(user.getLogin())) {
                log.debug("The user with this login is already registered.");
                return null;
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("The name is automatically assigned from the \"Login\" field. " +
                    "The new name is {}.", user.getLogin());
        }

        user.setId(userId++);
        users.put(user.getId(), user);
        log.debug("User {} added. The database contains {} user(s).", user.getLogin(), users.size());
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.validate(user);
        if (user.getId() != null && !users.containsKey(user.getId())) {
            log.debug("User ID {} missing. ", user.getId());
            throw new NullPointerException("User ID " + user.getId() + " missing.");
        }
        users.put(user.getId(), user);
        log.debug("User ID {} updated. ", user.getId());
        return user;
    }
}
