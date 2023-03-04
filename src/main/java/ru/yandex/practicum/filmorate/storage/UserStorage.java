package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User findUserById(Integer id);

    User updateUser(User user);

    User deleteUserById(Integer id);

    Collection<User> findAllUsers();

    Set<Integer> findAllUsersIds();

    void createPersonalFriendSet(Integer id);

    List<User> findPersonalFriendList(Integer id);

    Set<Integer> findPersonalFriendSetIds(Integer id);

    Set<Integer> updatePersonalFriendSet(Integer id, Set<Integer> personalFriendSet);

    Set<Integer> deletePersonalFriendSet(Integer id);

    Integer findCurrentId();

    Integer updateCurrentId(Integer id);
}
