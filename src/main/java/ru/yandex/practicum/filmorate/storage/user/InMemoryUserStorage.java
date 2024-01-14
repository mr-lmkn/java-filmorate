package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static Map<Integer, User> users = new HashMap<>();
    private static Integer usersMapKeyCounter = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUserById(Integer id) throws NoDataFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        String msg = String.format("Нет пользователя с 'id'=%s. Обновление не возможно.", id);
        log.warn(msg);
        throw new NoDataFoundException(msg);
    }

    @Override
    public User createUser(User user) throws WrongUserDataException {
        Integer userId = ++usersMapKeyCounter;
        String doDo = "создание пользователя";
        log.info("Инициировано {}", doDo);
        user.setId(userId);
        users.put(userId, user);
        log.info("Операция {} выполнена уcпешно", doDo);
        return user;
    }

    @Override
    public User updateUser(User user) throws WrongUserDataException, NoDataFoundException {
        Integer userId = user.getId();
        String doDo;

        if (userId != null) {
            doDo = "обновление пользователя";
            log.info("Инициировано {} {}", doDo, userId);

            if (!users.containsKey(userId)) {
                String msg = String.format("Нет пользователя с 'id' %s. Обновление не возможно.", userId);
                log.info(msg);
                throw new NoDataFoundException(msg);
            }

            users.put(userId, user);
            log.info("Операция {} выполнена уcпешно", doDo);

            return user;
        }

        String msg = String.format("Не указан 'id' %s. Обновление не возможно.", userId);
        log.info(msg);
        throw new WrongUserDataException(msg);

    }

    @Override
    public void delete(Integer id) throws WrongUserDataException {
        if (!users.containsKey(id)) {
            String msg = String.format("Нет пользователя с ID %s", id);
            log.info(msg);
            throw new WrongUserDataException(msg);
        } else {
            users.remove(id);
            log.info("Пользователь {} удален", id);
        }
    }

    @Override
    public User addFriend(int userId, int friendId) throws NoDataFoundException {
        User user;
        User userFriend;
        Set<Integer> emptyFriendsSet = new HashSet<>();
        Set<Integer> mayBeFriendsSet;
        String msg;

        if (users.containsKey(userId)) {
            user = users.get(userId);
            mayBeFriendsSet = Optional.ofNullable(user.getFriends()).orElse(emptyFriendsSet);
        } else {
            msg = String.format("Пользователь не найден ID %s", userId);
            log.info(msg);
            throw new NoDataFoundException("msg");
        }

        if (users.containsKey(friendId)) {
            mayBeFriendsSet.add(friendId);
            user.setFriends(mayBeFriendsSet);
        } else {
            msg = String.format("Пользователь (друг) не найден ID %s", userId);
            log.info(msg);
            throw new NoDataFoundException("msg");
        }

        return user;
    }

    @Override
    public User deteteFriend(int userId, int friendId) throws NoDataFoundException {
        User user;
        Set<Integer> friendsSet;
        String msg;

        if (!users.containsKey(userId)) {
            msg = String.format("Пользователь не найден ID %s", userId);
            log.info(msg);
            throw new NoDataFoundException("msg");
        }

        user = users.get(userId);
        try {
            friendsSet = user.getFriends();
            friendsSet.remove(friendId);
            user.setFriends(friendsSet);
        } catch (NullPointerException e) {
            msg = String.format("У пользователя нет друга ID %s", userId);
            log.info(msg);
            throw new NoDataFoundException("msg");
        }

        return user;
    }

    @Override
    public ArrayList<Integer> getAllUserFriends(int userId) throws NoDataFoundException {
        String msg;

        if (!users.containsKey(userId)) {
            msg = String.format("Пользователь не найден ID %s", userId);
            log.info(msg);
            throw new NoDataFoundException("msg");
        }

        try {
            return new ArrayList<Integer>(users.get(userId).getFriends());
        } catch (NullPointerException ignored) {
        }

        return null;
    }

    public void flushUsers() {
        users = new HashMap<>();
        usersMapKeyCounter = 0;
    }
}
