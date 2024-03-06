package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/films", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    @Value("${ru.yandex.practicum.filmorate.controller.popularFilmsLimitDefaultValue}")
    private Integer popularFilmsLimitDefaultValue;

    private final FilmService films;

    @GetMapping(produces = "application/json;")
    public List<Film> getAll() {
        log.info("Got all films request");
        return films.getAllFilms();
    }

    @GetMapping(value = "/{id}", produces = "application/json;")
    public Film getUser(@Valid @PathVariable Integer id) throws NoDataFoundException {
        log.info("Got user request");
        return films.getFilmById(id);
    }

    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public Film add(@Valid @RequestBody Film film)
            throws WrongFilmDataException, NoDataFoundException {
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

    @PutMapping(value = "/{filmId}/like/{userId}", produces = "application/json;")
    public Film update(@Valid @PathVariable Integer filmId, @Valid @PathVariable Integer userId)
            throws WrongFilmDataException, NoDataFoundException {
        log.info("Got add like from user {} to film {} request", userId, filmId);
        return films.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}", produces = "application/json;")
    public ResponseEntity<String> delete(@Valid @PathVariable Integer filmId, @Valid @PathVariable Integer userId)
            throws WrongFilmDataException, NoDataFoundException {
        log.info("Got delete like of user {} from film {} request", userId, filmId);
        films.deleteLike(filmId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = {"/popular"}, produces = "application/json;")
    public List<Film> getPopular(@Valid @RequestParam Optional<Integer> count)
            throws NoDataFoundException {
        Integer limit;
        if (count.isPresent()) {
            limit = count.get();
        } else {
            limit = popularFilmsLimitDefaultValue;
        }
        log.info("Got popular films list request. Limit is set to: {}", limit);
        return films.getPopular(limit);
    }

    @GetMapping("/director/{directorId}")
    @Validated
    public List<Film> getFilmsByDirector(@Valid @PathVariable Integer directorId,
                                         @RequestParam(defaultValue = "year") String sortBy)
                                                                 throws NoDataFoundException {

        return films.getFilmsByDirector(directorId, sortBy);
    }

}
