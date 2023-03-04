package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.validation.AfterDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    int id;
    @NotBlank
    String name;
    @NotNull
    @Size(max = 200, message = "validation.name.size.too_long")
    String description;

    @NotNull
    @AfterDate("1895-12-28")
    LocalDate releaseDate;
    @NotNull
    @Positive
    Integer duration;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
