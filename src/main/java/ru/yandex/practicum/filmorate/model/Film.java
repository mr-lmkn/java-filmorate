package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.MinReleaseDateConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    // целочисленный идентификатор
    @Min(value = 1, message = "id должен быть > 1")
    @Nullable
    private Integer id;

    // название
    @NotBlank(message = "название не может быть пустым")
    private String name;

    // описание
    @Length(max = 200, message = "максимальная длина описания — 200 символов")
    private String description;

    //дата релиза
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @MinReleaseDateConstraint
    private LocalDate releaseDate;

    // продолжительность фильма
    @Min(value = 1, message = "продолжительность фильма должна быть положительной.")
    private Integer duration;

}
