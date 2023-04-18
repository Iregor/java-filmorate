package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> createReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> deleteReview(Long reviewId);

    Optional<Review> findReviewById(Long userId);

    Collection<Review> findAllReviewsByFilmId(Long filmId, Integer count);

    Collection<Review> findAllReviews(Integer count);

    Optional<ReviewMark> createReviewMark(Long reviewId, Long userId, Boolean isLike);

    Optional<ReviewMark> removeReviewMark(Long reviewId, Long userId, Boolean isLike);

    Optional<ReviewMark> findReviewMark(Long reviewId, Long userId, Boolean isLike);
}
