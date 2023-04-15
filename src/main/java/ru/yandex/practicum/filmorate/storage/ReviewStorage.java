package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> createReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> deleteReview(Long id);

    Optional<Review> findReviewById(Long userId);

    Collection<Review> findAllReviews(Long filmId, Long count);

    Optional<ReviewMark> addLikeToReview(Long id, Long userId);

    Optional<ReviewMark> addDislikeToReview(Long id, Long userId);

    Optional<ReviewMark> deleteReviewLike(Long id, Long userId);

    Optional<ReviewMark> deleteReviewDislike(Long id, Long userId);

    Optional<ReviewMark> findReviewMark(Long reviewId, Long userId, boolean isLike);
}
