package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(Integer id) throws NoDataFoundException;

    Film createFilm(Film film) throws WrongFilmDataException, NoDataFoundException;

    Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException;

    void delete(Integer id) throws WrongFilmDataException;

    Film addLike(Integer filmId, Integer userId) throws NoDataFoundException;

    Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException;

    List<Film> getPopular(Integer limit) throws NoDataFoundException;

    List<Film> findFilmsByDirector(Integer directorId, String sortBy) throws NoDataFoundException;

    List<Film> getSearch(String query, String by);

    List<Film> getCommonFavouriteFilms(Integer userId, Integer friendId);

    List<Film> getRecommendations(int userId) throws NoDataFoundException;
}
