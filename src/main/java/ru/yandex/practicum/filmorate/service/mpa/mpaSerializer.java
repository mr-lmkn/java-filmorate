package ru.yandex.practicum.filmorate.service.mpa;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;

@JsonComponent
public class mpaSerializer extends JsonSerializer<Mpa> {
    @Override
    public void serialize(Mpa mpa, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", mpa.getId());
        jsonGenerator.writeStringField("name", mpa.getRatingCode()); // Имя. Ну ок.
        jsonGenerator.writeEndObject();
    }

}
