package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> createReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> deleteReview(Long id);

    Optional<Review> findReviewById(Long userId);

    Collection<Review> findAllReviews(Long filmId, Long count);

    void addLikeToReview(Long id, Long userId);

    void addDislikeToReview(Long id, Long userId);

    void deleteReviewLike(Long id, Long userId);

    void deleteReviewDislike(Long id, Long userId);
}
