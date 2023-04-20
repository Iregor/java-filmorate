package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Positive(message = "Идентификатор не может быть отрицательным.")
    Long id;
    @Email(message = "Некорректный Email.")
    String email;
    @NotBlank(message = "Логин не может быть пустым.")
    @NotNull(message = "Логин не может быть пустым.")
    String login;
    String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();
    Set<Long> likeFilms = new HashSet<>();

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
