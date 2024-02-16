package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDaoStorageImpl;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() throws WrongUserDataException, NoDataFoundException {
        // Подготавливаем данные для теста
        User newUser = new User(
                1, // LONG ?
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1),
                null
        );
        UserDaoStorageImpl userStorage = new UserDaoStorageImpl(jdbcTemplate);
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUserById(1);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }
}