package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getAll() {
        Collection<Director> result = directorStorage.findAll();
        log.info("Found {} director(s)", result.size());
        return result;
    }

    public Director getById(Long directorId) {
        Optional<Director> result = directorStorage.findById(directorId);
        if (result.isEmpty()) {
            log.warn("Director {} is not found", directorId);
            throw new IncorrectObjectIdException(String.format("Director %d is not found.", directorId));
        }
        log.info("Director {} is found", result.get().getId());
        return result.get();
    }

    public Director createDirector(Director director) {
        Optional<Director> result = directorStorage.addDirector(director);
        if (result.isEmpty()) {
            log.warn("Director {} is not created", director.getName());
            throw new IncorrectObjectIdException(String.format("Director %s is not created.", director.getName()));
        }
        log.info("Director {} {} created", result.get().getId(), result.get().getName());
        return result.get();
    }

    public Director updateDirector(Director director) {
        Optional<Director> result = directorStorage.updateDirector(director);
        if (result.isEmpty()) {
            log.warn("Director {} {} is not updated", director.getId(), director.getName());
            throw new IncorrectObjectIdException(String.format("Director %d %s is not update.",
                    director.getId(), director.getName()));
        }
        log.info("Director {} {} updated", result.get().getId(), result.get().getName());
        return result.get();
    }

    public void deleteDirector(Long directorId) {
        Optional<Director> result = directorStorage.findById(directorId);
        if (result.isEmpty()) {
            log.warn("Director {} is not found.", directorId);
            throw new IncorrectParameterException(String.format("Director %d is not found.", directorId));
        }
        log.info("Director {} deleted", result.get().getName());
        directorStorage.removeDirector(directorId);
    }
}
