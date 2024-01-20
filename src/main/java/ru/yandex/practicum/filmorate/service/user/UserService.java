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

    User addFriend(int userId, int friendId) throws NoDataFoundException;

    User deteteFriend(int userId, int friendId) throws NoDataFoundException;

    ArrayList<User> getAllUserFriends(int userId) throws NoDataFoundException;

    ArrayList<User> getIntersectFriends(int userId, int compareUserId) throws NoDataFoundException;

}
