package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review createReview(Review review) {
        Optional<Review> result = reviewStorage.createReview(review);
        return null;
    }

    public Review updateReview(Review review) {
        return null;
    }

    public Review deleteReview(Long id) {
        return null;
    }

    public Review findReviewById(Long id) {
        return null;
    }

    public List<Review> findAllReviews(Long filmId, Long count) {
        return null;
    }

    public void addLikeToReview(Long id, Long userId) {
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(Long id, Long userId) {
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void deleteReviewLike(Long id, Long userId) {
        reviewStorage.deleteReviewLike(id, userId);
    }

    public void deleteReviewDislike(Long id, Long userId) {
        reviewStorage.deleteReviewDislike(id, userId);
    }
}
