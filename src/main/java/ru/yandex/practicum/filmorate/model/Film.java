package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private Integer id;

    @NotNull
    @NotBlank
    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Exclude
    private String description;

    @EqualsAndHashCode.Include
    private LocalDate releaseDate;

    @EqualsAndHashCode.Exclude
    private Duration duration;

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = Duration.ofMinutes(duration);
    }

    public Long getDuration() {
        return this.duration.toMinutes();
    }
}
