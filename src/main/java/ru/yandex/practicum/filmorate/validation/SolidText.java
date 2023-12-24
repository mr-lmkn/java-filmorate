package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = SolidTextValidator.class)
@Documented
public @interface SolidText {

    String message() default "Поле содержит пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}