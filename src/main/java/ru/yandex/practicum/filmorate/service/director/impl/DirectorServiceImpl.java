package ru.yandex.practicum.filmorate.service.director.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    @Override
    public Director findDirectorById(Integer id) throws NoDataFoundException {
        return directorStorage.find(id);
    }

    @Override
    public Director create(Director director) {
        int id = directorStorage.add(director);
        Director createdDirector = director.toBuilder().id(id).build();
        log.debug("Создан режиссер {}", createdDirector);
        return createdDirector;
    }

    @Override
    public void update(Director director) throws NoDataFoundException {
        directorStorage.update(director);
        log.debug("Обновлен режиссер {}", director);

    }

    @Override
    public void deleteDirectorById(Integer id) throws NoDataFoundException {
        directorStorage.delete(id);
        log.debug("Удален режиссер с id {}", id);

    }
}
