package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Genre {
    Integer id;
    String name;
}
