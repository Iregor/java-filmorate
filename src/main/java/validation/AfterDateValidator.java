package validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    String dateToCompare;

    @Override
    public void initialize(AfterDate annotation){
        this.dateToCompare = annotation.value();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context){
        return value.isAfter(LocalDate.parse(dateToCompare));
    }
}
