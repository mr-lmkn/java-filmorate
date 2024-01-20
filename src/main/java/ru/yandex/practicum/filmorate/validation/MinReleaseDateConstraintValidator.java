package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Qualifier("MinReleaseDateConstraintValidator")
@Component
public class MinReleaseDateConstraintValidator implements ConstraintValidator<MinReleaseDateConstraint, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // "1895-12-28";

    @Value("${ru.yandex.practicum.filmorate.validation.MIN_FILM_RELEASE_DATE}")
    private String MIN_FILM_RELEASE_DATE;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (MIN_FILM_RELEASE_DATE == null) {
            // Я так и не понял как сделать тест для этой валидации, есть идеи как это запустить?
            // Мок тест? Но я, хочу тестировать только валидацию
            // MIN_FILM_RELEASE_DATE - в тесте у меня всегда NULL
            MIN_FILM_RELEASE_DATE = "1895-12-28";
        }
        log.debug("Setting 'minReleaseDate' is set to {}", MIN_FILM_RELEASE_DATE);
        if (value != null) {
            log.debug("Compare {} to {}", MIN_FILM_RELEASE_DATE, value.toString());
            return value.isAfter(LocalDate.parse(MIN_FILM_RELEASE_DATE, formatter));
        }
        return true;
    }

    public String getMinReleaseDate() {
        return MIN_FILM_RELEASE_DATE;
    }

}

