package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private String rating_mvp;
    private Set<String> categories = new HashSet<>();
    private Set<Long> likes = new HashSet<>();

    public Film(String name, String description, String releaseDate, int duration, String rating_mvp) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate);
        this.duration = Duration.ofMinutes(duration);
        this.rating_mvp = rating_mvp;
    }

    public Long getDuration() {
        return this.duration.toMinutes();
    }
}
