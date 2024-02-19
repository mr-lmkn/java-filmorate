package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.Nullable;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Mpa {
    private Integer id;
    @Nullable
    private String ratingCode;
    @Nullable
    private String ratingName;
    @Nullable
    private String description;
}
