package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.WrongUserData;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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

    public Optional<User> getUserByLogin(String login) {
        // Думаю, эта функция будет заменена, хотел все-таки вернуть нулл из MAP. Получилось... Гм. мдеее.
        // Надобыло брать LIST
        // Надо бы посимпатичнее это вот написать
        User existsSameUser;
        try {
            existsSameUser = users.entrySet()
                    .stream()
                    .filter(a -> Objects.equals(a.getValue().getLogin(), login))
                    .findFirst()
                    .get()
                    .getValue();
        } catch (NoSuchElementException e) {
            existsSameUser = null;
        }
        return Optional.ofNullable(existsSameUser);
    }

    public User createOrUpdateUser(Optional<Integer> optionalId, User user) throws WrongUserData {
        // Не уверен, что это верное решение. Наверное, можно как-то использовать билдер и валидатор
        // Вместо этого класса или пихать это в "сервис"
        Integer userMapKey;
        Integer userId = user.getId();
        String login = user.getLogin();
        String name = user.getName();
        String doDo = "";
        Optional<User> existsSameUser = getUserByLogin(login);

        if (name.isEmpty()) user.setName(login); //Имя заменяем на логин, если пустое

        if (optionalId.isPresent()) {
            doDo = "обновление пользователя";
            userMapKey = optionalId.get();
            log.info("Инициировано {} {}", doDo, userMapKey);
            boolean notSameId = !userMapKey.equals(userId);

            if (userMapKey < 0 || notSameId) {
                String msg = String.format("Ошибка заполнения поля 'id' %s != %s ", userMapKey, userId);
                log.info(msg);
                throw new WrongUserData(msg);
            }

            if (!users.containsKey(userMapKey)) {
                String msg = String.format("Нет пользователя с 'id' %s. Обновление не возможно.", userMapKey);
                log.info(msg);
                throw new WrongUserData(msg);
            }

            if (existsSameUser.isPresent()) {
                if (existsSameUser.get().getId() != userMapKey) {
                    String msg = String.format("Не возможно обновить пользователя логин %s занят", login);
                    log.info(msg);
                    throw new WrongUserData(msg);
                }
            }

        } else {
            doDo = "создание пользователя";
            log.info("Инициировано {}", doDo);
            if (existsSameUser.isPresent()) {
                String msg = String.format("Не возможно создать пользователя логин %s занят ", login);
                log.info(msg);
                throw new WrongUserData(msg);
            }
            userMapKey = ++usersMapKeyCounter;
            user.setId(usersMapKeyCounter);
        }

        users.put(userMapKey, user);
        log.info("Операция {} выполнена уcпешно", doDo);

        return user;
    }

    public void delete(Integer id) throws WrongUserData {
        if (!users.containsKey(id)) {
            String msg = String.format("Нет пользователя с ID %s", id);
            log.info(msg);
            throw new WrongUserData(msg);
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
