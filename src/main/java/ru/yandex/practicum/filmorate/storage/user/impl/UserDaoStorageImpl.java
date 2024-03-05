package ru.yandex.practicum.filmorate.storage.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Repository
@AllArgsConstructor
@Slf4j
public class UserDaoStorageImpl implements UserStorage {
    private final JdbcTemplate dataSource;

    @Override
    public List<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        SqlRowSet userRows = dataSource.queryForRowSet("SELECT * FROM USERS");
        while (userRows.next()) {
            User user = mapUserRow(userRows);
            users.add(user);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
        }
        log.info("Конец списка пользователей");
        return users;
    }

    @Override
    public User getUserById(Integer id) throws NoDataFoundException {
        SqlRowSet userRows = dataSource.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        if (userRows.next()) {
            User user = mapUserRow(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            String msg = String.format("Нет пользователя с 'id'=%s.", id);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }
    }

    @Override
    public User createUser(User user) throws WrongUserDataException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        Map<String, Object> parameters = mapUserQueryParameters(user);
        Integer id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        log.info("Generated id - " + id);
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) throws WrongUserDataException, NoDataFoundException {
        Integer id = user.getId();
        String doDo = "обновление пользователя";
        log.info("Инициировано {} {}", doDo, id);

        if (id != null && id > 0) {
            Integer updaterRows = dataSource.update(
                    "UPDATE USERS SET LOGIN = ?,"
                            + "    EMAIL = ?,"
                            + "    USER_NAME = ?,"
                            + "    BIRTHDAY = ?"
                            + "WHERE USER_ID = ?",
                    user.getLogin(),
                    user.getEmail(),
                    user.getName(),
                    user.getBirthday(),
                    id);

            if (updaterRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                return user;
            } else {
                String msg = String.format("Нет пользователя с 'id' %s. Обновление не возможно.", id);
                log.info(msg);
                throw new NoDataFoundException(msg);
            }
        }

        String msg = String.format("Не указан 'id' %s. Обновление не возможно.", id);
        log.info(msg);
        throw new WrongUserDataException(msg);

    }

    @Override
    public void delete(Integer id) throws WrongUserDataException {
        String doDo = " удаление пользователя";
        log.info("Инициировано {} {}", doDo, id);
        String msg;

        if (id != null && id > 0) {
            Integer deleteLikesRows = dataSource.update(
                    "DELETE FROM FILM_LIKES WHERE USER_ID = ?",
                    id);

            Integer deleteUserRows = dataSource.update(
                    "DELETE FROM USERS WHERE USER_ID = ?",
                    id);

            if (deleteUserRows > 0) {
                log.info("Пользователь {} удален", id);
                return;
            } else {
                msg = String.format("Нет пользователя с ID %s", id);
                log.info(msg);
                throw new WrongUserDataException(msg);
            }
        }

        msg = String.format("Не указан 'id' %s. Удаление не возможно.", id);
        log.info(msg);
        throw new WrongUserDataException(msg);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId, boolean confirmed) throws NoDataFoundException {
        Integer insertedRows = 0;
        if (userId != null && userId > 0) {
            String merge = String.format(
                    "MERGE INTO USER_FRIENDS u "
                            + " USING VALUES (?, ?) as d(USER_ID, FRIEND_ID)"
                            + " ON u.USER_ID = d.USER_ID AND u.FRIEND_ID = d.FRIEND_ID "
                            + " WHEN NOT MATCHED THEN "
                            + "   INSERT (USER_ID, FRIEND_ID, CONFIRMED) VALUES(?, ?, ?)"
                            + " WHEN MATCHED THEN "
                            + "   UPDATE SET USER_ID = ?, FRIEND_ID = ?, CONFIRMED = ?;");

            try {
                insertedRows = dataSource.update(
                        merge,
                        userId, friendId, // merged key data
                        userId, friendId, confirmed, // insert
                        userId, friendId, confirmed // update
                );
            } catch (DataIntegrityViolationException e) {
                String msg = String.format("Ошибка записи дружбы %s с %s.", userId, friendId);
                log.info(msg);
                throw new NoDataFoundException(msg);
            }

            if (insertedRows > 0) {
                log.info("Для {} добавлен друг {}", userId, friendId);
            }
            return getUserById(userId);

        }

        return getUserById(userId);

    }

    @Override
    public User confirmFriend(Integer userId, Integer friendId) throws NoDataFoundException {
        String doDo = "подтверждение дружбы пользователя";
        log.info("Инициировано {} {} и {}", doDo, userId, friendId);

        if (userId != null && userId > 0) {
            Integer updaterRows = dataSource.update(
                    "UPDATE USER_FRIENDS "
                            + " SET CONFIRMED = ? "
                            + " WHERE USER_ID = ? "
                            + " AND FRIEND_ID = ? ",
                    true,
                    userId,
                    friendId);

            addFriend(friendId, userId, true);

            if (updaterRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                return getUserById(userId);
            }

            String msg = String.format("Нет записи о дружбе %s с %s. Обновление не возможно.", userId, friendId);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }

        String msg = String.format("Нет пользователя с 'id' %s. Обновление не возможно.", userId);
        log.info(msg);
        throw new NoDataFoundException(msg);

    }

    @Override
    public User deteteFriend(Integer userId, Integer friendId) throws NoDataFoundException {
        String doDo = "удаление записи о дружбе";
        log.info("Инициировано {} {} и {}", doDo, userId, friendId);

        if (userId != null && userId > 0) {
            Integer updaterRows = dataSource.update(
                    "DELETE FROM USER_FRIENDS "
                            + " WHERE USER_ID = ?"
                            + " AND FRIEND_ID = ?",
                    userId,
                    friendId);

            if (updaterRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                return getUserById(userId);
            }

            String msg = String.format("Нет записи о дружбе %s с %s. Удаление не возможно.", userId, friendId);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }

        String msg = String.format("Нет пользователя с 'id' %s. Удаление не возможно.", userId);
        log.info(msg);
        throw new NoDataFoundException(msg);

    }

    @Override
    public ArrayList<Integer> getAllUserFriends(Integer userId) throws NoDataFoundException {
        ArrayList<Integer> users = new ArrayList<Integer>();
        SqlRowSet friendsRows = dataSource.queryForRowSet(
                "SELECT f.* "
                        + " FROM USER_FRIENDS f "
                        + " WHERE f.USER_ID = ? "
                        + " AND CONFIRMED = true; ",
                userId);
        while (friendsRows.next()) {
            Integer friendId = friendsRows.getInt("FRIEND_ID");
            users.add(friendId);
            log.info("Найден друг {} пользователя {}", userId, friendId);
        }
        log.info("Конец списка друзей...");
        return users;
    }

    @Override
    public void deteteUser(Integer userId) throws NoDataFoundException {
        String doDo = "удаление пользователя";
        log.info("Инициировано {} {}", doDo, userId);

        if (userId != null && userId > 0) {
            dataSource.update(
                    "DELETE FROM FILM_LIKES "
                            + " WHERE USER_ID = ?",
                    userId);
            dataSource.update(
                    "DELETE FROM USER_FRIENDS "
                            + " WHERE USER_ID = ? OR FRIEND_ID = ?",
                    userId,
                    userId);

            Integer deletedRows = dataSource.update(
                    "DELETE FROM USERS "
                            + " WHERE USER_ID = ?",
                    userId);

            if (deletedRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                return;
            }

            String msg = String.format("Нет записи о пользователе %s. Удаление не возможно.", userId);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }

        String msg = String.format("Нет пользователя с 'id' %s. Удаление не возможно.", userId);
        log.info(msg);
        throw new NoDataFoundException(msg);

    }

    private User mapUserRow(SqlRowSet userRows) {
        Integer userId = userRows.getInt("USER_ID");
        Set<Integer> userFriendsList = new HashSet<Integer>();
        try {
            userFriendsList = new HashSet<>(getAllUserFriends(userId));
        } catch (NoDataFoundException ignored) {
        }

        User user = new User(
                userId,
                userRows.getString("EMAIL"),
                userRows.getString("LOGIN"),
                userRows.getString("USER_NAME"),
                userRows.getDate("BIRTHDAY").toLocalDate(),
                userFriendsList);
        return user;
    }

    private Map<String, Object> mapUserQueryParameters(User user) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("USER_ID", user.getId());
        parameters.put("EMAIL", user.getEmail());
        parameters.put("LOGIN", user.getLogin());
        parameters.put("USER_NAME", user.getName());
        parameters.put("BIRTHDAY", user.getBirthday());
        return parameters;
    }

}
