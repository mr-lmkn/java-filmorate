package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Qualifier("MinReleaseDateConstraintValidator")
public class MinReleaseDateConstraintValidator implements ConstraintValidator<MinReleaseDateConstraint, LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // "1895-12-28";
    private static final String minReleaseDate = "1895-12-28";

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
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

