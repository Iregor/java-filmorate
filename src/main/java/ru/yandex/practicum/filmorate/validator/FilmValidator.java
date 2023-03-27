package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmValidator {
    public static final int MAX_LENGTH_DESCRIPTION = 200;
    public static final int MIN_DURATION_OF_FILM = 0;
    public static final LocalDate OLDEST_DATE_RELEASE
            = LocalDate.of(1895, 12, 28);

    public static void validate(Film film) {
        StringBuilder exceptionMessage = new StringBuilder();

        if (film.getName() == null || film.getName().isBlank()) {
            exceptionMessage.append("Названия фильма не может быть пустым. ");
        }
        if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            exceptionMessage.append("Описание фильма не может превышать ")
                    .append(MAX_LENGTH_DESCRIPTION)
                    .append(" знаков. ");
        }
        if (OLDEST_DATE_RELEASE.isAfter(film.getReleaseDate())) {
            exceptionMessage.append("Дата релиза не может быть раньше ")
                    .append(OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                    .append("г. ");
        }

        if (LocalDate.now().isBefore(film.getReleaseDate())) {
            exceptionMessage.append("Дата релиза не может быть позже текущего момента. ");
        }

        if (!(film.getDuration() > MIN_DURATION_OF_FILM)) {
            exceptionMessage.append("Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
        }

        if (!exceptionMessage.toString().isBlank()) {
            throw new ValidateException(exceptionMessage.toString());
        }
    }
}
