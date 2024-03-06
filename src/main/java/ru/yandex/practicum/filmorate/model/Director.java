package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Director {
    private Integer id;
    @NotBlank
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("director_id", id);
        values.put("director_name", name);

        return values;
    }
}
