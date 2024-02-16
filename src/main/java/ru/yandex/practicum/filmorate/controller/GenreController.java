package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@AllArgsConstructor
public class GenreController {

    private GenreService genreService;

    @GetMapping()
    public List<Genre> getAll() {
        log.info("Got all genre request");
        return genreService.getAllGenre();
    }

    @GetMapping(value = "/{id}")
    public Genre getMpa(@Valid @PathVariable Integer id) throws NoDataFoundException {
        log.info("Got genre request");
        return genreService.getGenreById(id);
    }

}
