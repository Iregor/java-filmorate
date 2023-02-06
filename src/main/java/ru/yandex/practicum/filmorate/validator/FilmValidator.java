package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmValidator {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate OLDEST_DATE_RELEASE
            = LocalDate.of(1895, 12, 28);

    public static boolean validate(Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidateException("Названия фильма не может быть пустым.");
        }
        if(film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidateException("Описание фильма не может превышать "
                    + MAX_LENGTH_DESCRIPTION + " знаков");
        }
        if(OLDEST_DATE_RELEASE.isAfter(film.getReleaseDate())) {
            throw new ValidateException("Дата релиза не может быть раньше "
                    + OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "г.");
        }
        if(film.getDuration() < 0) {
            throw new ValidateException("Продолжительность фильма не может быть отрицательной. ");
        }
        return true;
    }
}
