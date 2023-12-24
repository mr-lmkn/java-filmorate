package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MinReleaseDateValidator implements ConstraintValidator<MinReleaseDate, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${ru.yandex.practicum.filmorate.validation.minReleaseDate}")
    String minReleaseDate;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        log.debug("Min release date is set to {}", minReleaseDate);
        if (value != null) {
            return value.isAfter(LocalDate.parse(minReleaseDate, formatter));
        }
        return true;
    }

}

