package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static long userId = 1;
    private final HashMap<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn(String.format("User %d is not found.", userId));
            return null;
        }
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        UserValidator.validate(user);
        for (User userFromBase : users.values()) {
            if (userFromBase.getEmail().equals(user.getEmail())) {
                log.info("The user with this email is already registered.");
                return null;
            }
            if (userFromBase.getLogin().equals(user.getLogin())) {
                log.info("The user with this login is already registered.");
                return null;
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("The name is automatically assigned from the \"Login\" field. " +
                    "The new name is {}.", user.getLogin());
        }

        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User \"{}\" added. The database contains {} user(s).", user.getLogin(), users.size());
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.validate(user);

        if (user.getId() != null && !users.containsKey(user.getId())) {
            log.info("User ID {} missing. ", user.getId());
            throw new NullPointerException("User ID " + user.getId() + " missing.");
        }

        users.put(user.getId(), user);
        log.info("User ID {} updated. ", user.getId());
        return user;
    }
}
