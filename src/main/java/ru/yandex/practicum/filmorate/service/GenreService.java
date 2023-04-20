package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        List<Genre> result = genreStorage.findAll();
        log.info("Found {} genre(s).",
                result.size());
        return result;
    }

    public Genre findById(Long genreId) {
        Optional<Genre> result = genreStorage.findById(genreId);
        if (result.isEmpty()) {
            log.warn("Genre {} is not found.", genreId);
            throw new IncorrectObjectIdException(String.format("Genre %d is not found.",
                    genreId));
        }
        log.info("Genre {} is found.",
                result.get().getId());
        return result.get();
    }

    public Genre create(Genre genre) {
        Optional<Genre> result = genreStorage.create(genre);
        if (result.isEmpty()) {
            log.warn("Genre {} is not created.",
                    genre.getName());
            throw new IncorrectObjectIdException(String.format("Genre %s is not created.",
                    genre.getName()));
        }
        log.info("Genre {} {} created.",
                result.get().getId(), result.get().getName());
        return result.get();
    }

    public Genre update(Genre genre) {
        Optional<Genre> result = genreStorage.update(genre);
        if (result.isEmpty()) {
            log.warn("Genre {} {} is not updated.",
                    genre.getId(), genre.getName());
            throw new IncorrectObjectIdException(String.format("Genre %d %s is not updated.",
                    genre.getId(), genre.getName()));
        }
        log.info("Genre {} {} updated.",
                result.get().getId(), result.get().getName());
        return result.get();
    }
}
