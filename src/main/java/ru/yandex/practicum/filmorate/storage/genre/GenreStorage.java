package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> getAllGenre();

    Genre getGenreById(Integer id) throws NoDataFoundException;

    Set<Genre> getGenresByFilmId(Integer filmId);

    Set<Genre> linkGenresToFilmId(Integer filmId, Set<Genre> setGenre) throws NoDataFoundException;

    Genre addGenreToFilmId(Integer filmId, Genre genre) throws NoDataFoundException;
}
