package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping(value ="{mpaId}", produces = APPLICATION_JSON_VALUE)
    public Mpa findById(@PathVariable Long mpaId) {
        if (mpaService.findById(mpaId).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Mpa %d is not found.", mpaId));
        }
        return mpaService.findById(mpaId).get();
    }
}
