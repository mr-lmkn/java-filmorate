package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Film.
 * название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */
@Data
@Builder
@EqualsAndHashCode
//@Valid
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    // целочисленный идентификатор
    @Min(value = 1, message = "id должен быть > 1")
    @Nullable
    Integer id;

    // название
    @NotBlank(message = "название не может быть пустым")
    String name;

    // описание
    @Length(max = 200, message = "максимальная длина описания — 200 символов")
    String description;

    //дата релиза
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @MinReleaseDate
    LocalDate releaseDate;

    // продолжительность фильма
    @Min(value = 1, message = "продолжительность фильма должна быть положительной.")
    Integer duration;

}
