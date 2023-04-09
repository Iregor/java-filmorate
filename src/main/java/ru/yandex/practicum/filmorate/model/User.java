package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Positive(message = "Идентификатор не может быть отрицательным.")
    private Long id;
    @Email(message = "Некорректный Email.")
    private String email;
    @NotBlank(message = "Логин не может быть пустым.")
    @NotNull(message = "Логин не может быть пустым.")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
    private Set<Long> likeFilms = new HashSet<>();
}
