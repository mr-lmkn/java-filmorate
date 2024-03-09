package ru.yandex.practicum.filmorate.config;


import org.springframework.beans.factory.annotation.Configurable;
import org.zalando.logbook.Logbook;

@Configurable
public class LogbookConfiguration {
    Logbook logbook = Logbook.builder().build();

}
