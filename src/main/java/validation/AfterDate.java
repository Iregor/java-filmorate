package validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {
    String value();
    String message() default "{Date should be after value date}";
    Class<?>[] groups() default{};
    Class<? extends Payload> [] payload() default {};
}
