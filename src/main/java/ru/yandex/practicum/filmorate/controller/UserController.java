package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService users;
    private final FeedService feed;

    @GetMapping()
    public List<User> getAll() {
        log.info("Got all users request");
        return users.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@Valid @PathVariable Integer id) throws NoDataFoundException {
        log.info("Got user request");
        return users.getUserById(id);
    }

    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public User create(@Valid @RequestBody User user)
            throws WrongUserDataException {
        log.info("Got user create request: {}", user);
        return users.createUser(user);
    }

    @PutMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public User update(@Valid @RequestBody User user)
            throws WrongUserDataException, NoDataFoundException {
        log.info("Got update user request: {}", user);
        return users.updateUser(user);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json;")
    public ResponseEntity<String> delete(@Valid @PathVariable Integer id)
            throws WrongUserDataException {
        log.info("Got delete user {} request", id);
        users.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/friends/{friendId}") // добавление в друзья
    public User addFriend(@PathVariable int id, @PathVariable int friendId) throws NoDataFoundException {
        log.info("Got add user {} friend {} request", id, friendId);
        return users.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/confirm/{friendId}") // подтверждение дружбы
    public User confirmFriend(@PathVariable int id, @PathVariable int friendId) throws NoDataFoundException {
        log.info("Got confirm user {} friend {} request", id, friendId);
        return users.confirmFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}") // удаление из друзей
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) throws NoDataFoundException {
        log.info("Got delete user {} friend {} request", id, friendId);
        return users.deteteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends") //возвращаем список пользователей, являющихся его друзьями
    public List<User> getFriends(@PathVariable int id) throws NoDataFoundException {
        log.info("Got all user {} friends request", id);
        return users.getAllUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{comparedUserId}") //список друзей, общих с другим пользователем
    public List<User> getIntersectFriends(@PathVariable int id, @PathVariable int comparedUserId)
            throws NoDataFoundException {
        log.info("Got all user {} friends request", id);
        return users.getIntersectFriends(id, comparedUserId);
    }

    @DeleteMapping("/{id}") // удаление из друзей
    public void deleteUser(@PathVariable int id) throws NoDataFoundException {
        log.info("Got delete user {} request", id);
        users.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations") //рекомендации
    public List<Film> getRecommendations(@PathVariable int id) throws NoDataFoundException {
        return users.getRecommendations(id);
    }

    @GetMapping("/{id}/feed") // удаление из друзей
    public List<FeedEvent> getFeed(@PathVariable int id) throws NoDataFoundException {
        log.info("Got user {} feed request", id);
        return feed.getEventsByUserId(id);
    }

}
