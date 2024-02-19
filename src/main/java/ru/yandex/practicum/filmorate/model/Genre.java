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
    private Integer id;
    private String name;
}
