package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MinReleaseDateConstraintValidator implements ConstraintValidator<MinReleaseDateConstraint, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // @Value("${ru.yandex.practicum.filmorate.validation.minReleaseDate}") -- Тест валится, не видит конфига
    // Приглось отключить. Почему так ? гм.
    String minReleaseDate = "1895-12-28";

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        log.debug("Setting 'ru.yandex.practicum.filmorate.validation.minReleaseDate' is set to {}", minReleaseDate);
        log.debug("Compare {} to {}", minReleaseDate.toString(), value.toString());
        if (value != null) {
            return value.isAfter(LocalDate.parse(minReleaseDate, formatter));
        }
        return true;
    }

}

