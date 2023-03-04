package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();
    private HashMap<Integer, Set<Integer>> friendLists = new HashMap<>();
    private int currentId = 0;

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User findUserById(Integer id){
        return users.get(id);
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User deleteUserById(Integer id) {                                                                                    //добавить отправку информации, что user не был найден
        return users.remove(id);
    }

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public Set<Integer> findAllUsersIds() {
        return users.keySet();
    }

    public void createPersonalFriendSet(Integer id) {
        friendLists.put(id, new HashSet<>());
    }

    public Set<Integer> findPersonalFriendSetIds(Integer id) {
        return friendLists.get(id);
    }

    public List<User> findPersonalFriendList(Integer id) {
        return friendLists.get(id).stream()
                .map(friendId -> users.get(friendId))
                .sorted(Comparator.comparingInt(user -> user.getId()))
                .collect(Collectors.toList());
    }

    public Set<Integer> updatePersonalFriendSet(Integer id, Set<Integer> personalFriendSet) {
        friendLists.put(id, personalFriendSet);
        return personalFriendSet;
    }

    public Set<Integer> deletePersonalFriendSet(Integer id) {
        return friendLists.remove(id);
    }

    public Integer findCurrentId(){
        return currentId;
    }

    public Integer updateCurrentId(Integer id) {
        return (currentId = id);
    }
}
