package ru.yandex.practicum.filmorate;

import controller.FilmController;
import model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerValidationTest {

    FilmController filmController;
    Film film1, film2, film3, film4, film5, film6, film7, film8, film9, film10;
    ResponseStatusException valExc;

    @BeforeEach
    public void createTestInitialData() {
        filmController = new FilmController();
        film1 = Film.builder()
                .name(null)
                .description("abc")
                .releaseDate(LocalDate.of(1991,1,1))
                .duration(100)
                .build();

        film2 = Film.builder()
                .name("abc")
                .description(null)
                .releaseDate(LocalDate.of(1991,1,1))
                .duration(100)
                .build();

        film3 = Film.builder()
                .name("abc")
                .description("abc")
                .releaseDate(null)
                .duration(100)
                .build();

        film4 = Film.builder()
                .name("abc")
                .description("abc")
                .releaseDate(LocalDate.of(1991, 1,1))
                .duration(null)
                .build();

        film5 = Film.builder()
                .name(" ")
                .description("abc")
                .releaseDate(LocalDate.of(1991, 1,1))
                .duration(100)
                .build();


        char[] charArr = new char[201];
        Arrays.fill(charArr, 'a');

        film6 = Film.builder()
                .name("abc")
                .description(new String(charArr))
                .releaseDate(LocalDate.of(1991, 1,1))
                .duration(100)
                .build();

        film7 = Film.builder()
                .name("abc")
                .description("abc")
                .releaseDate(LocalDate.of(1800, 1,1))
                .duration(100)
                .build();

        film8 = Film.builder()
                .name("abc")
                .description("abc")
                .releaseDate(LocalDate.of(1991, 1,1))
                .duration(-100)
                .build();

        film9 = Film.builder()
                .name("abc")
                .description("abc")
                .releaseDate(LocalDate.of(1991, 1,1))
                .duration(100)
                .build();
    }

    @Test
    public void nameValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film1));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film5));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void descriptionValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film2));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film6));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void releaseDateValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film3));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film7));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void durationDateValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film4));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film8));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void correctFilmValidationTest(){
        filmController.add(film9);
        assertTrue(filmController.films().contains(film9));
    }
}
