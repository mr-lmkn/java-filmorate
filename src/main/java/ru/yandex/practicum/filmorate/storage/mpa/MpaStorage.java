package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa getMpaById(Integer id) throws NoDataFoundException;

    List<Mpa> getAllMpa();
}
