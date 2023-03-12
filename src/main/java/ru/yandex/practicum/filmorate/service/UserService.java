package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@Validated
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        nameCheck(user);
        Integer currentId = userStorage.findCurrentId();
        user.setId(++currentId);
        userStorage.updateCurrentId(currentId);
        userStorage.createPersonalFriendSet(user.getId());
        log.info(user + " was successfully created.");
        return userStorage.createUser(user);
    }

    public User findUserById(Integer id) {
        log.info("User with id: " + id + " was successfully provided.");
        return userStorage.findUserById(id);
    }

    public User updateUser(User user) {
        nameCheck(user);
        log.info(user + " was successfully updated.");
        return userStorage.updateUser(user);
    }

    public User deleteUserById(Integer id) {
        deleteFromFriendsSets(id);
        userStorage.deletePersonalFriendSet(id);
        log.info("User with id: " + id + " was successfully deleted.");
        return userStorage.deleteUserById(id);
    }

    public Collection<User> findAllUsers(){
        log.info("All users list was successfully provided.");
        return userStorage.findAllUsers();
    }

    public Set<Integer> addFriend(Integer userId, Integer friendToAddId) {
        Set<Integer> usersFriendSet;
        Set<Integer> friendToAddFriendSet;
        if ((usersFriendSet = userStorage.findPersonalFriendSetIds(userId)).contains(friendToAddId)) {
            throw new ResponseStatusException(CONFLICT, "Users are already friends.");
        }
        friendToAddFriendSet = userStorage.findPersonalFriendSetIds(friendToAddId);
        usersFriendSet.add(friendToAddId);
        friendToAddFriendSet.add(userId);
        log.info("Users with id : " + userId + ", " + friendToAddId + " were successfully turn into friends.");
        userStorage.updatePersonalFriendSet(friendToAddId, friendToAddFriendSet);
        return userStorage.updatePersonalFriendSet(userId, usersFriendSet);
    }

    public Set<Integer> deleteFriend(Integer userId, Integer friendToRemoveId) {

        Set<Integer> usersFriendSet;
        Set<Integer> friendToRemoveFriendSet;
        if (!(usersFriendSet = userStorage.findPersonalFriendSetIds(userId)).contains(friendToRemoveId)) {
            throw new ResponseStatusException(CONFLICT, "Users are not friends.");
        }

        friendToRemoveFriendSet = userStorage.findPersonalFriendSetIds(friendToRemoveId);
        usersFriendSet.remove(friendToRemoveId);
        friendToRemoveFriendSet.remove(userId);
        log.info("Users with id : " + userId + ", " + friendToRemoveId + " were successfully refused their relation.");
        userStorage.updatePersonalFriendSet(friendToRemoveId, friendToRemoveFriendSet);
        return userStorage.updatePersonalFriendSet(userId, usersFriendSet);
    }

    public List<User> findPersonalFriendList (Integer userId) {
        log.info("Friend list of user with id: " + userId + " was successfully provided.");
        return userStorage.findPersonalFriendList(userId);
    }

    public List<User> findCommonFriends (Integer ... userIds) {
//        реализована логика с возможностью поиска общих друзей для любого количества пользователей
//        на входе метода id всех пользователей для поиска общих друзей
        HashMap<Integer, Integer> commonFriendsCounters = new HashMap<>();
        List<Integer> usersFriendsList = new ArrayList<>();

        for (Integer userId : userIds) {
            usersFriendsList.addAll(userStorage.findPersonalFriendSetIds(userId));
        }
        for (Integer friendId : usersFriendsList) {
            commonFriendsCounters.put(friendId, commonFriendsCounters.getOrDefault(friendId, 0) + 1);
        }

        log.info("Common friend list of users with id: " + Arrays.toString(userIds) + " was successfully provided.");
        return commonFriendsCounters.entrySet().stream()
                .filter(entry -> entry.getValue() == userIds.length)
                .map(entry -> userStorage.findUserById(entry.getKey()))
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    private void nameCheck(User user){
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
    }

    private void deleteFromFriendsSets(Integer userId) {
        Set<Integer> usersFriendSet = userStorage.findPersonalFriendSetIds(userId);
        for (Integer friendId : usersFriendSet) {
            Set<Integer> friendIdFriendSet = userStorage.findPersonalFriendSetIds(friendId);
            friendIdFriendSet.remove(userId);
            userStorage.updatePersonalFriendSet(friendId, friendIdFriendSet);
        }
    }
}
