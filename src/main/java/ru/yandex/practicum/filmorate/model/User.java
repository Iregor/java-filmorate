package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
    private Set<Long> likeFilms = new HashSet<>();

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = LocalDate.parse(birthday);
    }
}
