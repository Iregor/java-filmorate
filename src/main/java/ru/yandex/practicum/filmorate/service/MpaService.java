package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("mpaDb") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> findAll() {
        Collection<Mpa> result = mpaStorage.findAll();
        log.info("Found {} MPA rating(s).", result.size());
        return result;
    }

    public Optional<Mpa> findById(Long mpaId) {
        Optional<Mpa> result = mpaStorage.findById(mpaId);
        if (result.isEmpty()) {
            log.warn("MPA rating {} is not found.", mpaId);
            return result;
        }
        log.info("MPA rating {} is found.", result.get().getId());
        return result;
    }

    public Mpa create(Mpa mpa) {
        Mpa result = mpaStorage.create(mpa);
        log.info("MPA rating {} {} added.", result.getId(), result.getName());
        return result;
    }

    public Mpa update(Mpa mpa) {
        Mpa result = mpaStorage.update(mpa);
        log.info("MPA rating {} updated.", result.getId());
        return result;
    }
}
