package ru.yandex.practicum.filmorate.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage users;
    private FilmStorage films;


    @Override
    public List<User> getAllUsers() {
        log.info("Зарос всех пользователей");
        return users.getAllUsers();
    }

    @Override
    public User getUserById(Integer id) throws NoDataFoundException {
        log.info("Зарос пользователя");
        return users.getUserById(id);
    }

    @Override
    public User createUser(User user) throws WrongUserDataException {
        log.info("Зарос создания пользователя");
        return users.createUser(user);
    }

    @Override
    public User updateUser(User user) throws WrongUserDataException, NoDataFoundException {
        log.info("Зарос обновления пользователя");
        return users.updateUser(user);
    }

    @Override
    public void delete(Integer id) throws WrongUserDataException {
        log.info("Зарос удаления пользователя");
        users.delete(id);
    }

    public User addFriend(Integer userId, Integer friendId) throws NoDataFoundException {
        log.info("Зарос удаления друзей");
        users.addFriend(userId, friendId, true);
        users.addFriend(friendId, userId, false);
        return users.getUserById(userId);
    }

    public User confirmFriend(Integer userId, Integer friendId) throws NoDataFoundException {
        log.info("Зарос подтверждения дружбы");
        users.confirmFriend(userId, friendId);
        return users.getUserById(userId);
    }

    public User deteteFriend(Integer userId, Integer friendId) throws NoDataFoundException {
        log.info("Зарос удаления дружбы");
        return users.deteteFriend(userId, friendId);
    }

    public ArrayList<User> getAllUserFriends(Integer userId) throws NoDataFoundException {
        users.getUserById(userId); // Проверяем, что пользователь есть

        log.info("Зарос всех друзей пользователя");
        ArrayList<User> ret = new ArrayList<>();
        try {
            for (var friend : users.getAllUserFriends(userId)) {
                ret.add(users.getUserById(friend));
            }
        } catch (NullPointerException ignored) {
        }

        return ret;
    }

    @Override
    public ArrayList<User> getIntersectFriends(Integer userId, Integer compareUserId) throws NoDataFoundException {
        log.info("Зарос общих друзей");
        ArrayList<User> outList = new ArrayList<>();
        Collection<Integer> userFriends = users.getAllUserFriends(userId);
        Collection<Integer> compareUserFriends = users.getAllUserFriends(compareUserId);
        userFriends.retainAll(compareUserFriends);
        for (Integer friend : userFriends) {
            outList.add(users.getUserById(friend));
        }
        return outList;
    }

    @Override
    public void deleteUser(Integer userId) throws NoDataFoundException {
        log.info("Зарос удаления дружбы");
        users.deteteUser(userId);
    }

    @Override
    public List<Film> getRecommendations(Integer userId) throws NoDataFoundException {
        getUserById(userId);
        return films.getRecommendations(userId);
    }

}
