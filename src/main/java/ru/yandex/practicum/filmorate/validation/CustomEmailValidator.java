package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CustomEmailValidator implements ConstraintValidator<CustomEmail, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            //return !EmailValidator.getInstance().isValid(value);
            String regex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+"
                    + "(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(value).matches();
        }
        return true;
    }

}