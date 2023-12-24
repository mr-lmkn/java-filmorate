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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    /**
     * Похоже, это делать не нудно было. НО так или иначе есть вопрос 133-я строка
     */

    User user;
    private MockMvc mockMvc;
    private UserController controller;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        user = User.builder()
                .id(1)
                .email("xmail@mail.ru")
                .login("User_1_Login")
                .name("User-name")
                .birthday(LocalDate.of(2023, 01, 01))
                .build();
        controller = new UserController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void flush() {
        UserService.flushUsers();
    }

    @Test
    void getAllTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))//body(user, user.getClass())
                .exchange()
                .expectStatus().isOk();

        ArrayList userList = new ArrayList<>();
        userList.add(user);

        webClient
                .get().uri("/users")
                .exchange()
                .expectBodyList(User.class)
                .isEqualTo(userList);
    }

    @Test
    void getUserTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        webClient
                .get().uri("/users/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateTest(@Autowired WebTestClient webClient) {
        // Создаем
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        // Второй
        User user2 = User.builder()
                .id(2)
                .email("xmail@mail.ru")
                .login("User_2_Login")
                .name("User-name")
                .birthday(LocalDate.of(2023, 01, 01))
                .build();

        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user2))
                .exchange()
                .expectStatus().isOk();

        // Изменяем
        user2.setName("User_3_Login");
        webClient
                .post().uri("/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user2))
                .exchange()
                .expectStatus()
                .isOk().expectBody(User.class).isEqualTo(user2);

        // проверяем тот же логин
        user.setName("User_1_Login");
        webClient
                .post().uri("/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }

    @Test
    void deleteTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        webClient
                .delete().uri("/users/1")
                .exchange()
                .expectStatus()
                .isEqualTo(204);
    }


    /*Еще один способ*/
    @Test
    public void emailTest() throws Exception {
        MockHttpServletRequestBuilder mockMvcRequestBuilders;

        String tJson = "{\n" +
                "        \"id\":  \"1\",\n" +
                "        \"email\": \"hkjsdfmail.ru\",\n" +
                "        \"login\": \"xsdfcsdfdg1\",\n" +
                "        \"name\": \",m,\",\n" +
                "        \"birthday\": \"2023-12-01\"\n" +
                "}";

        mockMvcRequestBuilders = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tJson);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void birthDayTest() throws Exception {
        MockHttpServletRequestBuilder mockMvcRequestBuilders;

        String tJson = "{\n" +
                "        \"id\":  \"1\",\n" +
                "        \"email\": \"hkjsd@fmail.ru\",\n" +
                "        \"login\": \"xsdfcsdfdg1\",\n" +
                "        \"name\": \",m,\",\n" +
                "        \"birthday\": \"2024-12-01\"\n" +
                "}";

        mockMvcRequestBuilders = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tJson);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

}