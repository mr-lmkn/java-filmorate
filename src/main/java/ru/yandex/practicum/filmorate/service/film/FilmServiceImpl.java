package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Autowired
    FilmStorage films;
    @Autowired
    UserService userService;

    @Override
    public List<Film> getAllFilms() {
        log.info("Запрос фильмов");
        return new ArrayList<>(films.getAllFilms());
    }

    @Override
    public Film getFilmById(Integer id) throws NoDataFoundException {
        log.info("Запрос фильма {}", id);
        return films.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) throws WrongFilmDataException {
        log.info("Запрос создания фильма");
        return films.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException {
        log.info("Запрос обновления фильма");
        return films.updateFilm(film);
    }

    @Override
    public void delete(Integer id) throws WrongFilmDataException {
        films.delete(id);
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) throws NoDataFoundException {
        Film film = films.getFilmById(filmId);
        log.info("Запрос добавления лайка фильму {}", film);
        User user = userService.getUserById(userId);
        log.info("пользователя {}", user);
        Set<Integer> likes = new HashSet<>();
        if (film.getLikes() != null) {
            likes = film.getLikes();
        }
        //Set<Integer> likes = Optional.of(film.getLikes()).orElse(new HashSet<>()); -- Гм. а почему это не работает?
        likes.add(user.getId());
        film.setLikes(likes);
        return film;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException {
        Film film = films.getFilmById(filmId);
        User user = userService.getUserById(userId);
        Set<Integer> likes = Optional.of(film.getLikes()).orElse(new HashSet<>());
        likes.remove(user.getId());
        film.setLikes(likes);
        return film;
    }

    @Override
    public List<Film> getPopular(Integer limit) throws NoDataFoundException {
        log.info("Задано ограничение вывода: {}", limit);
        List<Film> filmsList = films.getAllFilms();

        List<Film> maxLiked = List.of(filmsList
                .stream()
                .sorted((Film f1, Film f2)
                        -> Integer.valueOf(f2.getLikes().size())
                        .compareTo(Integer.valueOf(f1.getLikes().size())))
                .collect(Collectors.toList())
                .stream()
                .limit(limit)
                .toArray(Film[]::new));

        return maxLiked;
    }

    @Override
    public void flushFilms() {
        films.flushFilms();
    }

}
