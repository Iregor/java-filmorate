package ru.yandex.practicum.filmorate.storage.impl.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@Component("friendInMemory")
public class FriendInMemoryStorage implements FriendStorage {

    private final UserStorage userStorage;

    @Autowired
    public FriendInMemoryStorage(@Qualifier("userInMemory") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void writeRow(Long userId, Long friendId) {
        userStorage.findById(userId).get().getFriends().add(friendId);
        userStorage.findById(friendId).get().getFriends().add(userId);
    }

    @Override
    public void deleteRow(Long userId, Long friendId) {
        userStorage.findById(userId).get().getFriends().remove(friendId);
        userStorage.findById(friendId).get().getFriends().remove(userId);
    }
}
