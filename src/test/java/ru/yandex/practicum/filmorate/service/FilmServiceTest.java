package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.WrongFilmData;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
class FilmServiceTest {

    Film film;
    FilmService filmService;

    @BeforeEach
    public void setUp() {
        filmService = new FilmService();
        film = Film.builder() //создали фильм
                .name("Фильм 1")
                .description("Описание фильма 1")
                .duration(100)
                .releaseDate(LocalDate.of(2023, 01, 01))
                .build();
    }

    @AfterEach
    void flush() {
        FilmService.flushFilms();
    }

    @Test
    public void createFilm() throws WrongFilmData {
        Film isfilm = filmService.createFilm(film);
        assertEquals(film, isfilm, "Фильм не создан");
    }

    @Test
    public void getFilm() throws WrongFilmData {
        Film isfilm = filmService.createFilm(film);
        Film filmOptional = filmService.getFilmById(1);
        Assertions.assertNotNull(filmOptional);
    }

    @Test
    public void getAllFilm() throws WrongFilmData {
        Film isfilm = filmService.createFilm(film);
        List<Film> filmList = filmService.getAllFilms();
        Assertions.assertNotNull(filmList);
    }

    @Test
    public void updateFilm() throws WrongFilmData {
        Film isfilm1 = filmService.createFilm(film);
        isfilm1.setName("sdfsdfsdfsdf");
        log.info(filmService.getAllFilms().toString());
        Film isfilm2 = filmService.updateFilm(isfilm1);

        assertEquals(isfilm1.getName(), isfilm2.getName(), "Фильм не обновлен");
    }

    @Test
    public void wrongDate() throws WrongFilmData {
        film.setReleaseDate(LocalDate.of(1, 01, 01));
        Film isfilm = filmService.createFilm(film);
        assertEquals(film, isfilm, "Фильм не создан");
    }

}