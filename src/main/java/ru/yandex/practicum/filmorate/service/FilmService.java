package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    public Collection<Film> findAll() {
        Collection<Film> result = filmStorage.findAll();
        log.info("Found {} movie(s).", result.size());
        addDataFilms(result);
        return result;
    }

    public Collection<Film> getPopularFilms(Integer size, Long genreId, String year) {
        Collection<Film> result;
        if (genreId != null && year != null) {
            result = filmStorage.findPopularFilmsByGenreIdAndYear(size, genreId, year);
        } else if (genreId != null) {
            result = filmStorage.findPopularFilmsByGenreId(size, genreId);
        } else if (year != null) {
            result = filmStorage.findPopularFilmsByYear(size, year);
        } else {
            result = filmStorage.findPopularFilms(size);
        }
        addDataFilms(result);
        log.info("Found {} movie(s).", result.size());
        return result;
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        Collection<Film> result = filmStorage.findCommonFilms(userId, friendId);
        log.info("Found {} film(s).", result.size());
        addDataFilms(result);
        return result;
    }

    public Set<Film> searchFilms(String subString, Set<String> by) {
        for (String param : by) {
            if (!(param.equals("director") || param.equals("title"))) {
                log.warn("Param {} is not correct.", param);
                throw new IncorrectParameterException(String.format("Param %s is not correct.", param));
            }
        }
        Set<Film> mergeCollect = new HashSet<>();
        if (by.contains("title")) {
            mergeCollect.addAll(filmStorage.searchFilmsByTitle(subString));
        }
        if (by.contains("director")) {
            mergeCollect.addAll(filmStorage.searchFilmsByDirector(subString));
        }
        addDataFilms(mergeCollect);
        log.info("Found {} film(s).", mergeCollect.size());
        return mergeCollect;
    }

    public Film findById(Long filmId) {
        Optional<Film> result = filmStorage.findById(filmId);
        if (result.isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", filmId));
        }
        addDataFilms(List.of(result.get()));
        log.info("Film {} is found.", result.get().getId());
        return result.get();
    }

    public Film create(Film film) {
        Optional<Film> result = filmStorage.create(film);
        if (result.isEmpty()) {
            log.warn("Film {} is not created.",
                    film.getName());
            throw new IncorrectObjectIdException(String.format("Film %s is not created.",
                    film.getName()));
        }
        film.getGenres().forEach(genre -> genreStorage.add(result.get().getId(), genre.getId()));
        film.getDirectors().forEach(director -> directorStorage.addInFilm(result.get().getId(), director.getId()));
        addDataFilms(List.of(result.get()));
        log.info("Film {} {} created.",
                result.get().getId(), result.get().getName());
        return result.get();
    }

    public Film update(Film film) {
        Optional<Film> result = filmStorage.update(film);
        if (result.isEmpty()) {
            log.warn("Film {} {} is not updated.",
                    film.getId(), film.getName());
            throw new IncorrectObjectIdException(String.format("Film %d %s is not updated.",
                    film.getId(), film.getName()));
        }

        updateGenreByFilm(film);
        updateDirectorByFilm(film);
        addDataFilms(List.of(result.get()));
        log.info("Film {} {} updated.",
                result.get().getId(), result.get().getName());

        return result.get();
    }

    public void delete(Long filmId) {
        Optional<Film> result = filmStorage.findById(filmId);
        if (result.isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", filmId));
        }
        filmStorage.remove(filmId);
        log.info("Film {} removed.", result.get().getName());
    }

    public void like(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %s is not found.", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("Friend %s is not found.", userId));
        }
        likesStorage.add(filmId, userId);
        log.info("User {} liked film {}.", userId, filmId);
        eventService.addEvent(Event.builder()
                .eventId(null)
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
    }

    public void dislike(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %s is not found.", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        likesStorage.remove(filmId, userId);
        log.info("User {} disliked film {}.", userId, filmId);
        eventService.addEvent(Event.builder()
                .eventId(null)
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
    }

    public Collection<Film> convertIdsToFilms(Collection<Long> filmsIds) {
        Collection<Film> sortedFilm = new ArrayList<>();
        Collection<Film> resultAdvise = filmStorage.filmsByIds(filmsIds);
        addDataFilms(resultAdvise);
        Map<Long, Film> filmMap = resultAdvise.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        for (Long id : filmsIds) {
            sortedFilm.add(filmMap.get(id));
        }
        log.info("A list of recommended films, has been created.");
        return sortedFilm;
    }

    private void updateGenreByFilm(Film film) {
        Set<Genre> removedGenre = genreStorage.findByFilmId(film.getId())
                .stream()
                .filter(genre -> !film.getGenres().contains(genre))
                .collect(Collectors.toSet());
        Set<Genre> addedGenre = film.getGenres()
                .stream()
                .filter(genre -> !genreStorage.findByFilmId(film.getId()).contains(genre))
                .collect(Collectors.toSet());
        removedGenre.forEach(genre -> genreStorage.remove(film.getId(), genre.getId()));
        addedGenre.forEach(genre -> genreStorage.add(film.getId(), genre.getId()));
    }

    public Collection<Film> getFilmDirectorSorted(Long directorId, String sortBy) {
        log.info("Get Films by Director with id = {}, sorted by {}", directorId, sortBy);
        if (directorStorage.findById(directorId).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Director %s is not found.", directorId));
        }
        switch (sortBy) {
            case "year": {
                Collection<Film> result = filmStorage.findFilmsDirectorByYear(directorId);
                addDataFilms(result);
                return result;
            }
            case "likes": {
                Collection<Film> result = filmStorage.findFilmsDirectorByLikes(directorId);
                addDataFilms(result);
                return result;
            }
            default:
                throw new IncorrectParameterException(
                        String.format("Invalid sort parameter %s .", sortBy));
        }
    }

    private void updateDirectorByFilm(Film film) {
        Set<Director> removedDirector = directorStorage.findByFilmId(film.getId())
                .stream()
                .filter(director -> !film.getDirectors().contains(director))
                .collect(Collectors.toSet());
        Set<Director> addedDirector = film.getDirectors()
                .stream()
                .filter(director -> !directorStorage.findByFilmId(film.getId()).contains(director))
                .collect(Collectors.toSet());
        removedDirector.forEach(director -> directorStorage.removeFromFilm(film.getId(), director.getId()));
        addedDirector.forEach(director -> directorStorage.addInFilm(film.getId(), director.getId()));
    }

    private void addDataFilms(Collection<Film> films) {
        Map<Long, Film> filmsMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Map<Long, Set<Genre>> genresMap = genreStorage.findByFilms(filmsMap.keySet());
        Map<Long, Set<Long>> likesMap = likesStorage.findByFilms(filmsMap.keySet());
        Map<Long, Set<Director>> directorsMap = directorStorage.findByFilms(filmsMap.keySet());
        films.forEach(film -> {
            film.setGenres(new HashSet<>());
            film.setLikes(new HashSet<>());
            film.setDirectors(new HashSet<>());
            if (Objects.requireNonNull(genresMap).containsKey(film.getId())) {
                film.setGenres(genresMap.get(film.getId()));
            }
            if (Objects.requireNonNull(likesMap).containsKey(film.getId())) {
                film.setLikes(likesMap.get(film.getId()));
            }
            if (Objects.requireNonNull(directorsMap).containsKey(film.getId())) {
                film.setDirectors(directorsMap.get(film.getId()));
            }
        });
    }
}

