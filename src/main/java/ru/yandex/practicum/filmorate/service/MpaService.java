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
        return mpaStorage.findAll();
    }

    public Optional<Mpa> findById(Long id) {
        return mpaStorage.findById(id);
    }

    public Mpa create(Mpa mpa) {
        return mpaStorage.create(mpa);
    }

    public Mpa update(Mpa mpa) {
        return mpaStorage.update(mpa);
    }
}
