package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank(message = "Логин не может быть пустым.")
    @NotNull(message = "Логин не может быть пустым.")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
    private Set<Long> likeFilms = new HashSet<>();

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = LocalDate.parse(birthday);
    }

    public User(Long id, String email, String login, String name, String birthday) {
        this(email, login, name, birthday);
        this.id = id;
    }
}
