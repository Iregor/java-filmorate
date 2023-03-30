package ru.yandex.practicum.filmorate.validator.after;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterValidator implements ConstraintValidator<After, LocalDate> {

    private String after;

    @Override
    public void initialize(After after) {
        this.after = after.value();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        return !localDate.isBefore(LocalDate.parse(after));
    }
}

