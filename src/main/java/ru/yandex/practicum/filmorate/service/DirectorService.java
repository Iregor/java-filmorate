package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
public class DirectorService {

    public Collection<Director> findAll() {
        return null;
    }

    public Director findById(Long id) {
        return null;
    }

    public Director createDirector(Director director) {
        return director;
    }

    public Director updateDirector(Director director) {
        return director;
    }

    public void deleteDirector(Long id) {
    }
}
