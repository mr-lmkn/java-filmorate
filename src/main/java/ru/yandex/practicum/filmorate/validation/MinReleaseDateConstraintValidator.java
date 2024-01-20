package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Qualifier("MinReleaseDateConstraintValidator")
public class MinReleaseDateConstraintValidator implements ConstraintValidator<MinReleaseDateConstraint, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // "1895-12-28";

    @Value("${ru.yandex.practicum.filmorate.validation.minReleaseDate}")
    private String minReleaseDate;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (minReleaseDate == null) {
            // Я так и не понял как сделать тест для этой валидации, есть идеи как это запустить?
            // Мок тест? Но я, хочу тестировать только валидацию
            // minReleaseDate - в тесте у меня всегда NULL
            minReleaseDate = "1895-12-28";
        }
        log.debug("Setting 'minReleaseDate' is set to {}", minReleaseDate);
        if (value != null) {
            log.debug("Compare {} to {}", minReleaseDate, value.toString());
            return value.isAfter(LocalDate.parse(minReleaseDate, formatter));
        }
        return true;
    }

    public String getMinReleaseDate() {
        return minReleaseDate;
    }

}

