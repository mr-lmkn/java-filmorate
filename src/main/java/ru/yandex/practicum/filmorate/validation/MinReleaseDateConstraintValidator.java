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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //private String minReleaseDate = "1895-12-28";
    @Value("${ru.yandex.practicum.filmorate.validation.filmMinReleaseDate}")
    private String filmMinReleaseDate;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        log.debug("Setting 'minReleaseDate' is set to {}", filmMinReleaseDate);
        if (value != null) {
            log.debug("Compare {} to {}", filmMinReleaseDate, value.toString());
            return value.isAfter(LocalDate.parse(filmMinReleaseDate, formatter));
        }
        return true;
    }

}

