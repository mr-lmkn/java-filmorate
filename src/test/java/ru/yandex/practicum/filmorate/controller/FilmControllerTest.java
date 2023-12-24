package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
class FilmControllerTest {

    /**
     * Похоже, это делать не нудно было. Но хочется потренироваться
     */

    Film film;
    private MockMvc mockMvc;
    private FilmController controller;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        film = Film.builder()
                .name("Film-name")
                .description("Descripton")
                .duration(15)
                .build();
        controller = new FilmController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void flush() {
        FilmService.flushFilms();
    }

    @Test
    void getAllTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))//body(film, film.getClass())
                .exchange()
                .expectStatus().isOk();

        ArrayList filmList = new ArrayList<>();
        film.setId(1);
        filmList.add(film);

        webClient
                .get().uri("/films")
                .exchange()
                .expectBodyList(Film.class)
                .isEqualTo(filmList);
    }

    @Test
    void getfilmTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))
                .exchange()
                .expectStatus().isOk();

        webClient
                .get().uri("/films")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void cantUpdateTest(@Autowired WebTestClient webClient) {
        webClient
                .put().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void updateTest(@Autowired WebTestClient webClient) {
        // Создаем
        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))
                .exchange()
                .expectStatus().isOk();

        // Второй
        Film film2 = Film.builder()
                .id(2)
                .name("Film-name-2")
                .description("Descripton")
                .duration(15)
                .build();

        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film2))
                .exchange()
                .expectStatus().isOk();

        // Изменяем
        film2.setName("Film-name-3");
        webClient
                .put().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film2))
                .exchange()
                .expectStatus()
                .isOk().expectBody(Film.class).isEqualTo(film2);

        // проверяем тот же логин
        film2.setName("Film-name");
        webClient
                .put().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film2))
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void deleteTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(film))
                .exchange()
                .expectStatus().isOk();

        webClient
                .delete().uri("/films/1")
                .exchange()
                .expectStatus()
                .isEqualTo(204);
    }

}