package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer id) throws NoDataFoundException;

    User createUser(User user) throws WrongUserDataException;

    User updateUser(User user) throws WrongUserDataException, NoDataFoundException;

    void delete(Integer id) throws WrongUserDataException;

    User addFriend(Integer userId, Integer friendId) throws NoDataFoundException;

    User confirmFriend(Integer userId, Integer friendId) throws NoDataFoundException;

    User deteteFriend(Integer userId, Integer friendId) throws NoDataFoundException;

    ArrayList<User> getAllUserFriends(Integer userId) throws NoDataFoundException;

    ArrayList<User> getIntersectFriends(Integer userId, Integer compareUserId) throws NoDataFoundException;

    void deleteUser(Integer userId) throws NoDataFoundException;
}
