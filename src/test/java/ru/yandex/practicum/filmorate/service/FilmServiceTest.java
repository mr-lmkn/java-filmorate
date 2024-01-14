package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
class FilmServiceTest {

    Film film;
    @Autowired
    FilmService filmService;
    @Autowired
    UserService userService;

    @BeforeEach
    public void setUp() {
        film = Film.builder() //создали фильм
                .name("Фильм 1")
                .description("Описание фильма 1")
                .duration(100)
                .releaseDate(LocalDate.of(2023, 01, 01))
                .build();
    }

    @AfterEach
    void flush() {
        filmService.flushFilms();
    }

    @Test
    public void createFilm() throws WrongFilmDataException {
        Film isfilm = filmService.createFilm(film);
        assertEquals(film, isfilm, "Фильм не создан");
    }

    @Test
    public void getFilm() throws WrongFilmDataException, NoDataFoundException {
        Film isfilm = filmService.createFilm(film);
        Film filmOptional = filmService.getFilmById(1);
        Assertions.assertNotNull(filmOptional);
    }

    @Test
    public void getAllFilm() throws WrongFilmDataException {
        Film isfilm = filmService.createFilm(film);
        List<Film> filmList = filmService.getAllFilms();
        Assertions.assertNotNull(filmList);
    }

    @Test
    public void updateFilm() throws WrongFilmDataException, NoDataFoundException {
        Film isfilm1 = filmService.createFilm(film);
        isfilm1.setName("sdfsdfsdfsdf");
        log.info(filmService.getAllFilms().toString());
        Film isfilm2 = filmService.updateFilm(isfilm1);

        assertEquals(isfilm1.getName(), isfilm2.getName(), "Фильм не обновлен");
    }

    @Test
    public void wrongDate() throws WrongFilmDataException {
        film.setReleaseDate(LocalDate.of(1, 01, 01));
        Film isfilm = filmService.createFilm(film);
        assertEquals(film, isfilm, "Фильм не создан");
    }

}