package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/films", produces = "application/json")
@Slf4j
public class FilmController {

    private FilmService films = new FilmService();

    @GetMapping(produces = "application/json;")
    public List<Film> getAll() {
        log.info("Got all films request");
        return films.getAllFilms();
    }

    @GetMapping(value = "/{id}", produces = "application/json;")
    public Film getUser(@Valid @PathVariable Integer id) {
        log.info("Got user request");
        return films.getFilmById(id);
    }

    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public Film add(@Valid @RequestBody Film film)
            throws WrongFilmDataException {
        log.info("Got create film request: {} ", film);
        return films.createFilm(film);
    }

    @PutMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public Film update(@Valid @RequestBody Film film)
            throws WrongFilmDataException, NoDataFoundException {
        log.info("Got update film request: {} ", film);
        return films.updateFilm(film);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json;")
    public ResponseEntity<String> delete(@Valid @PathVariable Integer id)
            throws WrongFilmDataException {
        log.info("Got delete film {} request", id);
        films.delete(id);
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
    public Map<String, String> noDataFoundException(NoDataFoundException e) {
        return Collections.singletonMap("Error message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongFilmDataException.class)
    public Map<String, String> wrongFilmDataException(WrongFilmDataException e) {
        return Collections.singletonMap("Error message", e.getMessage());
    }
}
