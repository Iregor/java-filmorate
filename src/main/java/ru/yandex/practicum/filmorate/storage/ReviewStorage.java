package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> createReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> deleteReview(Long reviewId);

    Optional<Review> findReviewById(Long userId);

    Boolean isExistReview(Long userId, Long filmId);

    List<Review> findAllReviewsByFilmId(Long filmId, Integer count);

    List<Review> findAllReviews(Integer count);

    Optional<ReviewMark> createReviewMark(Long reviewId, Long userId, Boolean isLike);

    Optional<ReviewMark> removeReviewMark(Long reviewId, Long userId, Boolean isLike);

    Optional<ReviewMark> updateReviewMark(Long reviewId, Long userId, Boolean isLike);

    Optional<ReviewMark> findReviewMark(Long reviewId, Long userId, Boolean isLike);
}
