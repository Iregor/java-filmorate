package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface LikesStorage {

    void like(Long filmId, Long userId);

    void dislike(Long filmId, Long userId);

    Collection<Long> getUserLikes(Long userId);

    Collection<Long> getFilmLikes(Long filmId);
}
