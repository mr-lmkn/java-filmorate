package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    Mpa getMpaById(Integer id) throws NoDataFoundException;

    List<Mpa> getAllMpa();
}
