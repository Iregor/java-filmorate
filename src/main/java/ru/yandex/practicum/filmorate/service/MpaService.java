package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        Collection<Mpa> result = mpaStorage.findAll();
        log.info("Found {} MPA rating(s).",
                result.size());
        return result;
    }

    public Mpa findById(Long mpaId) {
        Optional<Mpa> result = mpaStorage.findById(mpaId);
        if (result.isEmpty()) {
            log.warn("MPA rating {} is not found.", mpaId);
            throw new IncorrectObjectIdException(String.format("Mpa %d is not found.",
                    mpaId));
        }
        log.info("MPA rating {} is found.",
                result.get().getId());
        return result.get();
    }
}
