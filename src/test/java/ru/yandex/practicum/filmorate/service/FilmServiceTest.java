package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
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
        HashSet genres = new HashSet<Genre>();
        genres.add(new Genre(1, "Комедия"));
        Mpa mpa = new Mpa().builder()
                .id(1)
                .ratingCode("G")
                .ratingName("General Audiences")
                .description("All ages admitted. Nothing that would offend parents for viewing by children.")
                .build();

        film = Film.builder() //создали фильм
                .name("Фильм 1")
                .description("Описание фильма 1")
                .duration(100)
                .releaseDate(LocalDate.of(2023, 01, 01))
                .genres(genres)
                .mpa(mpa)
                .build();
    }

    @Test
    public void createFilm() throws WrongFilmDataException, NoDataFoundException {
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
    public void getAllFilm() throws WrongFilmDataException, NoDataFoundException {
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
    public void wrongDate() throws WrongFilmDataException, NoDataFoundException {
        film.setReleaseDate(LocalDate.of(1905, 01, 01));
        Film isfilm = filmService.createFilm(film);
        assertEquals(film, isfilm, "Фильм не создан");
    }

}