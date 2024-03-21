package ru.yandex.practicum.filmorate.service.genre.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Genre getGenreById(Integer id) throws NoDataFoundException {
        log.info("Зарос рейтинга");
        return genreStorage.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenre() {
        log.info("Зарос всех рейтинов");
        return genreStorage.getAllGenre();
    }
}

