package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;
    public static final String REQUEST_GET_LOG = "Получен запрос к эндпойнту: 'GET {}";
    public static final String REQUEST_POST_LOG = "Получен запрос к эндпойнту: 'POST {}";
    public static final String REQUEST_PUT_LOG = "Получен запрос к эндпойнту: 'PUT {}";
    public static final String REQUEST_DELETE_LOG = "Получен запрос к эндпойнту: 'DELETE {}";

    @GetMapping
    public Collection<Director> findAll(HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable("id") Long id, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return service.findById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody Director director, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director, HttpServletRequest request) {
        log.info(REQUEST_PUT_LOG, request.getRequestURI());
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Long id, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        service.deleteDirector(id);
    }
}
