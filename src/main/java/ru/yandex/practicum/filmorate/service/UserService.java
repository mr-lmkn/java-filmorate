package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserService {
    private static Map<Integer, User> users = new HashMap<>();
    private static Integer usersMapKeyCounter = 0;

    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        log.warn("Пользователя с ИД {} - нет в системе", id);
        return null;
    }

    public User createUser(User user) throws WrongUserDataException {
        Integer userId = ++usersMapKeyCounter;
        String doDo = "создание пользователя";
        log.info("Инициировано {}", doDo);
        user.setId(userId);
        users.put(userId, user);
        log.info("Операция {} выполнена уcпешно", doDo);
        return user;
    }

    public User updateUser(User user) throws WrongUserDataException, NoDataFoundException {
        Integer userId = user.getId();;
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

    public static void flushUsers() {
        users = new HashMap<>();
        usersMapKeyCounter = 0;
    }

}
