package ru.yandex.practicum.filmorate.service.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FeedService feed;

    @Value("${ru.yandex.practicum.filmorate.service.FilmServiceImpl.popularFilmsLimitDefaultValue}")
    private Integer popularFilmsLimitDefaultValue;

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
        Film outFilm = filmStorage.addLike(filmId, userId);
        feed.saveEvent(userId, FeedEventType.LIKE, FeedEventOperation.ADD, filmId);
        return outFilm;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException {
        log.info("Запрос удаления лайка фильма");
        Film outFilm = filmStorage.deleteLike(filmId, userId);
        feed.saveEvent(userId, FeedEventType.LIKE, FeedEventOperation.REMOVE, filmId);
        return outFilm;
    }

    @Override
    public List<Film> getPopular(Optional<Integer> count, String genreId, Integer year) throws NoDataFoundException {
        Integer limit;
        if (count.isPresent()) {
            limit = count.get();
        } else {
            limit = popularFilmsLimitDefaultValue;
        }

        log.info("Запрос популярных фильмов");
        log.info("Задано ограничение вывода: {}", limit);
        if (genreId != null & year != null) {
            log.info("Фильтарция популярных фильмов по году и жанру");
        }
        if (genreId == null & year != null) {
            log.info("Фильтарция популярных фильмов по году");
        }
        if (genreId != null & year == null) {
            log.info("Фильтарция популярных фильмов по жанру");
        }
        return filmStorage.getPopular(limit, genreId, year);
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) throws NoDataFoundException {
        return filmStorage.findFilmsByDirector(directorId, sortBy);
    }

    @Override
    public List<Film> getSearch(String query, String by) {
        log.info("Запрос поиска фильмов");
        log.info("Вариант поиска: {}", by);
        return filmStorage.getSearch(query, by);
    }

    @Override
    public List<Film> getCommonFavouriteFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFavouriteFilms(userId, friendId);
    }

}
