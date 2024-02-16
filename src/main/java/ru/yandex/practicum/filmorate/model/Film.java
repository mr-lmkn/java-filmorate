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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    // целочисленный идентификатор
    @Min(value = 1L, message = "id должен быть > 1")
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

    @Nullable
    private Mpa mpa;

    @Nullable
    private Set<Genre> genres;

    @Nullable
    private Set<Integer> likes;


    public Set<Genre> getGenres() {
        Set<Genre> outGenres;
        if (genres != null) {
            outGenres = genres;
        } else {
            outGenres = new HashSet<>();
        }
        return outGenres;
    }

    public Set<Integer> getLikes() {
        Set<Integer> outLikes;
        if (likes != null) {
            outLikes = likes;
        } else {
            outLikes = new HashSet<>();
        }
        return outLikes;
    }


}
