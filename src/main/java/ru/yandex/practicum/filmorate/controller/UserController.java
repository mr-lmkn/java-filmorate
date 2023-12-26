package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserService users = new UserService();

    @GetMapping()
    public List<User> getAll() {
        log.info("Got all users request");
        return users.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@Valid @PathVariable Integer id) {
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoDataFoundException.class)
    public String noDataFoundException(NoDataFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongUserDataException.class)
    public String wrongUserDataException(WrongUserDataException e) {
        return e.getMessage();
    }

}
