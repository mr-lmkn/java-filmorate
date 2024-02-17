package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
@EnableWebMvc
class UserControllerTest {

    User user;
    private MockMvc mockMvc;
    @Autowired
    private UserController controller;
    @Autowired
    private UserService serService;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        user = User.builder()
                .email("xmail@mail.ru")
                .login("User_1_Login")
                .name("User-name")
                .birthday(LocalDate.of(2023, 01, 01))
                .build();

        //this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        // WebApplicationContext webApplicationContext = new StaticWebApplicationContext();
        // this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAllTest(@Autowired WebTestClient webClient) {
        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))//body(user, user.getClass())
                .exchange();

        ArrayList userList = new ArrayList<>();
        user.setId(1);
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
                .email("xmail2@mail.ru")
                .login("User_2_Login")
                .name("User-name2")
                .birthday(LocalDate.of(2023, 01, 01))
                .build();

        webClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user2))
                .exchange()
                .expectStatus().isOk();

        // Изменяем
        user2.setId(2);
        user2.setName("User_3_Login");
        webClient
                .put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user2))
                .exchange()
                .expectStatus()
                .isOk().expectBody(User.class).isEqualTo(user2);

        // проверяем тот же логин
        user.setName("User_1_Login");
        webClient
                .put().uri("/users")
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

    @Test
    public void emailTest() throws Exception {
        MockHttpServletRequestBuilder mockMvcRequestBuilders;
        user.setEmail("hkjsdfmail.ru");
        String tJson = objectMapper.writeValueAsString(user);

        mockMvcRequestBuilders = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tJson);
        //Последние два теста явно косячные.
        MockMvc mockMvc2;
        mockMvc2 = MockMvcBuilders.standaloneSetup(controller).build();
        mockMvc2.perform(mockMvcRequestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    public void birthDayTest() throws Exception {
        MockHttpServletRequestBuilder mockMvcRequestBuilders;
        user.setBirthday(LocalDate.of(2026, 01, 01));

        String tJson = objectMapper.writeValueAsString(user);

        mockMvcRequestBuilders = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tJson);

        MockMvc mockMvc2;
        mockMvc2 = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc2.perform(mockMvcRequestBuilders)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();


    }

}