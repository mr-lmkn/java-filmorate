package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Constraint(validatedBy = UserNameConstraintValidator.class)
@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
public @interface UserNameConstraint {

    String message() default "Не получилось обновить имя пользователя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
