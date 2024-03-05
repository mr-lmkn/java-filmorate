package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User getUserById(Integer id) throws NoDataFoundException;

    User createUser(User user) throws WrongUserDataException;

    User updateUser(User user) throws WrongUserDataException, NoDataFoundException;

    void delete(Integer id) throws WrongUserDataException;

    User addFriend(Integer userId, Integer friendId, boolean confirmed) throws NoDataFoundException;

    User confirmFriend(Integer userId, Integer friendId) throws NoDataFoundException;

    User deteteFriend(Integer userId, Integer friendId) throws NoDataFoundException;

    ArrayList<Integer> getAllUserFriends(Integer userId) throws NoDataFoundException;

    void deteteUser(Integer userId) throws NoDataFoundException;

}
