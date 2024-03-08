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
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/popular"}, produces = "application/json;")
    public List<Film> getPopular(@Valid @RequestParam Optional<Integer> count, @RequestParam(value = "genreId",
            required = false) String genreId, @RequestParam(value = "year", required = false) Integer year)
            throws NoDataFoundException {
        Integer limit;
        if (count.isPresent()) {
            limit = count.get();
        } else {
            limit = popularFilmsLimitDefaultValue;
        }
        log.info("Got popular films list request. Limit is set to: {}", limit);
        return films.getPopular(limit, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    @Validated
    public List<Film> getFilmsByDirector(@PathVariable Integer directorId,
                                         @RequestParam(defaultValue = "year") String sortBy)
            throws NoDataFoundException {

        return films.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping(value = {"/search"}, produces = "application/json;")
    public List<Film> getSearch(@RequestParam(value = "query") String query, @RequestParam(value = "by",
            defaultValue = "director,title", required = false) String by) {
        return films.getSearch(query, by);
    }

    @GetMapping("/common")
    @Validated
    public List<Film> getCommonFavouriteFilms(@Valid @RequestParam Integer userId,
                                              @RequestParam Integer friendId) {
        return films.getCommonFavouriteFilms(userId, friendId);
    }
}
