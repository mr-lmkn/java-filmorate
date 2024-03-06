package ru.yandex.practicum.filmorate.service.film.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private FilmStorage filmStorage;

    @Override
    public List<Film> getAllFilms() {
        log.info("Запрос фильмов");
        return new ArrayList<>(filmStorage.getAllFilms());
    }

    @Override
    public Film getFilmById(Integer id) throws NoDataFoundException {
        log.info("Запрос фильма {}", id);
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) throws WrongFilmDataException, NoDataFoundException {
        log.info("Запрос создания фильма");
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException {
        log.info("Запрос обновления фильма");
        return filmStorage.updateFilm(film);
    }

    @Override
    public void delete(Integer id) throws WrongFilmDataException {
        log.info("Запрос удаления фильма");
        filmStorage.delete(id);
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) throws NoDataFoundException {
        log.info("Запрос добавления лайка фильму");
        return filmStorage.addLike(filmId, userId);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException {
        log.info("Запрос удаления лайка фильма");
        return filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(Integer limit) throws NoDataFoundException {
        log.info("Запрос популярных фильмов");
        log.info("Задано ограничение вывода: {}", limit);
        return filmStorage.getPopular(limit);
    }

    public List<Film> getRecommendations(Integer userId) throws NoDataFoundException {
        return filmStorage.getRecommendations(userId);
    }

}
