package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage users;

    @Override
    public List<User> getAllUsers() {
        log.info("Зарос создания всех пользователей");
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
        log.info("Зарос создания пользователя");
        return users.updateUser(user);
    }

    @Override
    public void delete(Integer id) throws WrongUserDataException {
        users.delete(id);
    }

    public User addFriend(int userId, int friendId) throws NoDataFoundException {
        users.addFriend(userId, friendId);
        return users.getUserById(userId);
    }

    public User deteteFriend(int userId, int friendId) throws NoDataFoundException {
        return users.deteteFriend(userId, friendId);
    }

    public ArrayList<User> getAllUserFriends(int userId) throws NoDataFoundException {
        ArrayList<User> ret = new ArrayList<>();
        try {
            for (Integer friend : users.getAllUserFriends(userId)) {
                ret.add(users.getUserById(friend));
            }
        } catch (NullPointerException ignored) {
        }
        return ret;
    }

    @Override
    public ArrayList<User> getIntersectFriends(int userId, int compareUserId) throws NoDataFoundException {
        ArrayList<User> outList = new ArrayList<User>();
        ArrayList<Integer> userFriends = users.getAllUserFriends(userId);
        ArrayList<Integer> compareUserFriends = users.getAllUserFriends(compareUserId);
        // Если что-то пустое - вернем пустой список
        if (compareUserFriends == null || userFriends.isEmpty() || compareUserFriends.isEmpty()) {
            return outList;
        }
        // Нужны только пересекающиеся пользователи
        for (int friend : userFriends) {
            if (compareUserFriends.contains(friend)) {
                outList.add(users.getUserById(friend));
            }
        }
        return outList;
    }

    @Override
    public void flushUsers() {
        users.flushUsers();
    }

}
