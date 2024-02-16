package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre getGenreById(Integer id) throws NoDataFoundException;

    List<Genre> getAllGenre();
}
