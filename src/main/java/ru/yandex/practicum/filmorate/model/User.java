package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.UserNameConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@UserNameConstraint
public class User {

    //целочисленный идентификатор
    @Min(value = 1, message = "id должен быть > 1")
    @Nullable
    private Integer id;

    //электронная почта
    @NotBlank(message = "Поле 'E-mail' не заполнено")
    @Email(regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+"
            + "(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@"
            + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Поле e-mail должно содержать валидный адрес")
    private String email;

    // логин пользователя
    @NotBlank(message = "Поле 'Login' не может быть пустым")
    @Pattern(regexp = ".*[^\\h]", message = "Поле содержит пробелы")
    private String login;

    // имя для отображения
    private String name;

    // дата рождения
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

}
