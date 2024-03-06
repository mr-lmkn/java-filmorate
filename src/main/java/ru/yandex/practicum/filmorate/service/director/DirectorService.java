package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> findAll();

    Director findDirectorById(Integer id) throws NoDataFoundException;

    Director create(Director director);

    void update(Director director) throws NoDataFoundException;

    void deleteDirectorById(Integer id) throws NoDataFoundException;
}
