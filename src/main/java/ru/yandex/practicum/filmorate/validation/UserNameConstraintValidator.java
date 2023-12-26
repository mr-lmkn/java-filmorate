package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNameConstraintValidator implements ConstraintValidator<UserNameConstraint, User> {

    public boolean isValid(User object, ConstraintValidatorContext context) {
        if (!(object instanceof User)) {
            throw new IllegalArgumentException("@UserConstraint only applies to User objects");
        }
        User user = (User) object;
        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
            user.setName(user.getLogin());
        }
        return true;
    }

}
