package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    @Qualifier("genreDb")
    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        Collection<Genre> result = genreStorage.readAll();
        log.info("Found {} genre(s).", result.size());
        return result;
    }

    public Genre findById(Long genreId) {
        Optional<Genre> result = genreStorage.readById(genreId);
        if (result.isEmpty()) {
            log.warn("Genre {} is not found.", genreId);
            throw new IncorrectObjectIdException(String.format("Genre %d is not found.", genreId));
        }
        log.info("Genre {} is found.", result.get().getId());
        return result.get();
    }

    public Genre create(Genre genre) {
        Genre result = genreStorage.writeRow(genre);
        log.info("Genre {} {} added.", result.getId(), result.getName());
        return result;
    }

    public Genre update(Genre genre) {
        if (genreStorage.readById(genre.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Genre %d is not found.", genre.getId()));
        }
        Genre result = genreStorage.updateRow(genre);
        log.info("Genre {} updated.", result.getId());
        return result;
    }
}
