package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerValidationTest {
    private static Validator validator;
    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }
    FilmController filmController;
    Set<ConstraintViolation<Film>> violations;
    Film film1, film2, film3, film4, film5, film6, film7, film8, film9, film10;
    ResponseStatusException valExc;

    @BeforeEach
    public void createTestInitialData() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
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
        violations = validator.validate(film1);
        assertEquals(1, violations.size());

        violations = validator.validate(film5);
        assertEquals(1, violations.size());
    }

    @Test
    public void descriptionValidationTest(){

        violations = validator.validate(film2);
        assertEquals(1, violations.size());

        violations = validator.validate(film6);
        assertEquals(1, violations.size());
    }

    @Test
    public void releaseDateValidationTest(){
        violations = validator.validate(film3);
        assertEquals(1, violations.size());

        violations = validator.validate(film7);
        assertEquals(1, violations.size());
    }

    @Test
    public void durationDateValidationTest(){
        violations = validator.validate(film4);
        assertEquals(1, violations.size());

        violations = validator.validate(film8);
        assertEquals(1, violations.size());
    }

    @Test
    public void correctFilmValidationTest(){
        violations = validator.validate(film9);
        assertEquals(0, violations.size());

        filmController.createFilm(film9);
        assertTrue(filmController.findAllFilms().contains(film9));
    }
}
