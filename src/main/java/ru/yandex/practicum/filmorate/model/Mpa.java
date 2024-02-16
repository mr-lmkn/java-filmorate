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
    Integer id;
    @Nullable
    String ratingCode;
    @Nullable
    String ratingName;
    @Nullable
    String description;
}
