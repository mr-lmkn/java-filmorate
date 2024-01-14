package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmService {

    private static Map<Integer, Film> films = new HashMap<>();
    private static Integer filmsMapKeyCounter = 0;

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilmById(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        log.warn("Фильма с ИД {} - нет в системе", id);
        return null;
    }

    public Film createFilm(Film film) throws WrongFilmDataException {
        Integer filmId = ++filmsMapKeyCounter;
        log.info("Инициировано создание фильма");
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Операция создание фильма выполнена уcпешно");
        return film;
    }

    public Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException {
        Integer filmId = film.getId();
        String doDo;

        if (filmId != null) {
            doDo = "обновление фильма";
            log.info("Инициировано {} {}", doDo, filmId);

            if (!films.containsKey(filmId)) {
                String msg = String.format("Нет фильма с 'id' %s. Обновление не возможно.", filmId);
                log.info(msg);
                throw new NoDataFoundException(msg);
            }

            films.put(filmId, film);
            log.info("Операция {} выполнена уcпешно", doDo);

            return film;
        }

        String msg = String.format("Не указан 'id' %s. Обновление не возможно.", filmId);
        log.info(msg);
        throw new WrongFilmDataException(msg);
    }

    public void delete(Integer id) throws WrongFilmDataException {
        if (!films.containsKey(id)) {
            String msg = String.format("Нет фильма с ID %s", id);
            log.info(msg);
            throw new WrongFilmDataException(msg);
        } else {
            films.remove(id);
            log.info("Фильм {} удален", id);
        }
    }

    public static void flushFilms() {
        films = new HashMap<>();
        filmsMapKeyCounter = 0;
    }

}
