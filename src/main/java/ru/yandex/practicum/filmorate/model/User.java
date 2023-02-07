package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {


    private Integer id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = LocalDate.parse(birthday);
    }
}
