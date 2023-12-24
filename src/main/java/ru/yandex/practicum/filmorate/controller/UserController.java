package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.WrongUserData;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

/**
 * <H1>UserController<H1/>
 * создание пользователя;
 * обновление пользователя;
 * получение списка всех пользователей.
 * <p>
 * электронная почта не может быть пустой и должна содержать символ @;
 * логин не может быть пустым и содержать пробелы;
 * имя для отображения может быть пустым — в таком случае будет использован логин;
 * дата рождения не может быть в будущем.
 */

@RestController
//@ControllerAdvice extends ResponseEntityExceptionHandler
@RequestMapping("/users")
@Slf4j
public class UserController {

    UserService users = new UserService();

    @GetMapping(produces = "application/json;")
    public List<User> getAll() {
        log.info("Got all users request");
        return users.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@Valid @PathVariable Integer id) {
        log.info("Got user request");
        return users.getUserById(id);
    }
/*
    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public User create(@Valid @RequestBody User user) {
        log.info("Got user create request: {}", user);
        try {
            // HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // ((HttpServletResponse) response).setStatus(201);
            return users.createOrUpdateUser(Optional.ofNullable(null), user);
        } catch (WrongUserData er) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, er.getMessage(), er);
        }
    }
*/
    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public User update(@Valid @RequestBody User user) {
        log.info("Got create or update user request: {} -> {}", user);
        try {
            return users.createOrUpdateUser(user);
        } catch (WrongUserData er) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, er.getMessage(), er);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json;")
    ResponseEntity<String> delete(@Valid @PathVariable Integer id) {
        log.info("Got delete user {} request", id);
        try {
            users.delete(id);
            return ResponseEntity.noContent().build();
        } catch (WrongUserData er) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, er.getMessage(), er);
        }
        //return new ResponseEntity<>("message"+"Пользователь удален", HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
