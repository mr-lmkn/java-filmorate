package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director find(Integer id) throws NoDataFoundException;

    int add(Director director);

    void update(Director director) throws NoDataFoundException;

    void delete(Integer id) throws NoDataFoundException;
}
