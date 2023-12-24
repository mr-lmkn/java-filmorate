package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.WrongFilmData;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/films", produces = "application/json")
@Slf4j
public class FilmController {

    FilmService films = new FilmService();

    @GetMapping(produces = "application/json;")
    public List<Film> getAll() {
        log.info("Got all users request");
        return films.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getUser(@Valid @PathVariable Integer id) {
        log.info("Got user request");
        return films.getFilmById(id);
    }

    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Got create or update film request: {} ", film);
        try {
            return films.createOrUpdateFilm(film);
        } catch (WrongFilmData er) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, er.getMessage(), er);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json;")
    ResponseEntity<String> delete(@Valid @PathVariable Integer id) {
        log.info("Got delete film {} request", id);
        try {
            films.delete(id);
            return ResponseEntity.noContent().build();
        } catch (WrongFilmData er) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, er.getMessage(), er);
        }
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
