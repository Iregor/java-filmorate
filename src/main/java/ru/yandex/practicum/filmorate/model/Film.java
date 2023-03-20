package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Data
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    @NotNull(message = "Название фильма быть пустым.")
    private String name;
    @Length(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @After(message = "Дата не может быть раньше создания первого фильма.")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Продолжительность не может быть отрицательной.")
    private int duration;
    private int rate;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = duration;
    }

    public Film(Long id, String name, String description, String releaseDate,
                int duration, int rate, long mpaId) {
        this(name, description, releaseDate, duration);
        this.id = id;
        this.rate = rate;
        this.mpa = new Mpa(mpaId);
    }

    @Target({ FIELD })
    @Retention(RUNTIME)
    @Constraint(validatedBy = AfterValidator.class)
    @Documented
    public @interface After {
        String message() default "{After.invalid}";
        Class<?>[] groups() default { };
        Class<? extends Payload>[] payload() default { };
    }

    public static class AfterValidator implements ConstraintValidator<After, LocalDate> {
        public static final LocalDate OLDEST_DATE_RELEASE
                = LocalDate.of(1895, 12, 28);
        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            return !value.isBefore(OLDEST_DATE_RELEASE);
        }
    }
}
