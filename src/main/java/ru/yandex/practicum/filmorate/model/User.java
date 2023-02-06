package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;

    @Email
    @EqualsAndHashCode.Include
    private String email;

    @NotNull
    @NotBlank
    @EqualsAndHashCode.Include
    private String login;
    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
}
