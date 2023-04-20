package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public List<Director> getAll() {
        return service.getAll();
    }

    @GetMapping("/{directorId}")
    public Director getById(@PathVariable("directorId") Long directorId) {
        return service.getById(directorId);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable Long directorId) {
        service.deleteDirector(directorId);
    }
}
