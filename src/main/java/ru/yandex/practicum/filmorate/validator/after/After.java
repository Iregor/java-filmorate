package ru.yandex.practicum.filmorate.validator.after;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AfterValidator.class)
@Documented
public @interface After {
    String message() default "{After.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String value();
}