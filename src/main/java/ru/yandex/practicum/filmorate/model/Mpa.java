package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Mpa {
    private Long id;
    private String name;

    public Mpa(Long mpaId) {
        this.id = mpaId;
    }

    public Mpa(String name) {
        this.name = name;
    }
}
