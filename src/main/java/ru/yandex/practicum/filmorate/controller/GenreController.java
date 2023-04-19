package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping(value = "{genreId}")
    public Genre findById(@PathVariable Long genreId) {
        return genreService.findById(genreId);
    }

    @PostMapping
    public Genre create(@Valid @RequestBody Genre genre) {
        return genreService.create(genre);
    }

    @PutMapping
    public Genre update(@Valid @RequestBody Genre genre) {
        return genreService.update(genre);
    }
}
