package ru.yandex.practicum.filmorate.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {
    @EqualsAndHashCode.Include
    private Integer reviewId;

    @NotNull(message = "Content can't be null")
    @Size(min = 1, max = 500, message = "Content can't be longer than 500 characters")
    private String content;

    @NotNull(message = "isPositive can't be null")
    private Boolean isPositive;

    @NotNull(message = "userId can't be null")
    private Integer userId;

    @NotNull(message = "filmId can't be null")
    private Integer filmId;

    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        return values;
    }
}
