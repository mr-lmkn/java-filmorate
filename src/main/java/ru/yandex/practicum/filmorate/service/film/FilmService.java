package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    Film getFilmById(Integer id) throws NoDataFoundException;

    Film createFilm(Film film) throws WrongFilmDataException;

    Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException;

    void delete(Integer id) throws WrongFilmDataException;

    Film addLike(Integer filmId, Integer userId) throws NoDataFoundException; // пользователь ставит лайк фильму.

    Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException; // пользователь удаляет лайк.

    //— возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
    List<Film> getPopular(Integer limit) throws NoDataFoundException;

}
