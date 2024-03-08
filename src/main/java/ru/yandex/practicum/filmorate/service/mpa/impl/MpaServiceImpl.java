package ru.yandex.practicum.filmorate.service.mpa.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public Mpa getMpaById(Integer id) throws NoDataFoundException {
        log.info("Зарос рейтинга");
        return mpaStorage.getMpaById(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Зарос всех рейтинов");
        return mpaStorage.getAllMpa();
    }

}
