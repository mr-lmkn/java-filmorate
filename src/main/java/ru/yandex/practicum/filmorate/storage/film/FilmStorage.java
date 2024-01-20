package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(Integer id) throws NoDataFoundException;

    Film createFilm(Film film) throws WrongFilmDataException;

    Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException;

    void delete(Integer id) throws WrongFilmDataException;

}
