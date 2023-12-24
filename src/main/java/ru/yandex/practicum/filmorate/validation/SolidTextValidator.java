package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SolidTextValidator implements ConstraintValidator<SolidText, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            for (char c : value.toCharArray()) {
                if (Character.isWhitespace(c)) return false;
            }
        }
        return true;
    }

}