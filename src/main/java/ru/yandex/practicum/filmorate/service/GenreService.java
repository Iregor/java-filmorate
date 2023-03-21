package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("genreDb") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Collection<Genre> findAllByFilmId(Long filmId) {
        return genreStorage.findAllByFilmId(filmId);
    }

    public Optional<Genre> findById(Long id) {
        return genreStorage.findById(id);
    }

    public Genre create(Genre genre) {
        return genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        return genreStorage.update(genre);
    }
}
