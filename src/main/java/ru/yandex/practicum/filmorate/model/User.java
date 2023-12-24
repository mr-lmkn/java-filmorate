package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.SolidText;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * User
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //целочисленный идентификатор
    @Min(value = 1, message = "id должен быть > 1")
    @Nullable
    private Integer id;

    //электронная почта
    @NotBlank(message = "Поле 'E-mail' не заполнено")
    @Email(message = "Поле 'E-mail' не содержит символ '@'")
    private String email;

    // логин пользователя
    @NotBlank(message = "Поле 'Login' не заполнено")
    @SolidText
    private String login;

    // имя для отображения
    @NotNull
    private String name;

    // дата рождения
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

}
