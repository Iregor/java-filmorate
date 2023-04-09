package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.after.After;

import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    @NotNull(message = "Название фильма быть пустым.")
    private String name;
    @Length(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @After(value = "1895-12-28")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Продолжительность не может быть отрицательной.")
    private int duration;
    private int rate;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();
}
