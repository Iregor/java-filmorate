package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public Collection<Director> findAll() {
        return storage.findAll();
    }

    public Director findById(Long id) {
        Optional<Director> optionalDirector = storage.findById(id);
        if (optionalDirector.isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Директор %d не найден.", id));
        }
        return optionalDirector.get();
    }

    public Director createDirector(Director director) {
        Optional<Director> optionalDirector = storage.createDirector(director);
        if (optionalDirector.isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Директор %s не создан.", director.getName()));
        }
        return optionalDirector.get();
    }

    public Director updateDirector(Director director) {
        Optional<Director> optionalDirector = storage.updateDirector(director);
        if (optionalDirector.isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Директор %d %s не обновлен.",
                    director.getId(), director.getName()));
        }
        return optionalDirector.get();
    }

    public void deleteDirector(Long id) {
        storage.deleteDirector(id);
    }
}
