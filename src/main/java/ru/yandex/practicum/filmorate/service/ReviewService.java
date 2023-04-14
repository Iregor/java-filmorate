package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review createReview(Review review) {
        assertUserExists(review.getUserId());
        assertFilmExists(review.getFilmId());
        Optional<Review> result = reviewStorage.createReview(review);
        if (result.isEmpty()) {
            log.warn("Review of user: {} for film: {} is not created.", review.getUserId(), review.getFilmId());
            throw new IncorrectObjectIdException(String.format("Review of user %d for film %d is not created.",
                    review.getUserId(), review.getFilmId()));
        }
        return result.get();
    }

    public Review updateReview(Review review) {
        assertUserExists(review.getUserId());
        assertFilmExists(review.getFilmId());
        assertReviewExists(review.getReviewId());
        Optional<Review> result = reviewStorage.updateReview(review);
        return result.get();
    }

    public void deleteReview(Long id) {
        assertReviewExists(id);
        Optional<Review> result = reviewStorage.deleteReview(id);
        if (result.isPresent()) {
            log.warn("Review with id: {} is not deleted.", id);
            throw new IncorrectObjectIdException(String.format("Review with id: %d is not deleted.", id));
        }
    }

    public Review findReviewById(Long id) {
        Optional<Review> result = reviewStorage.findReviewById(id);
        if (result.isEmpty()) {
            log.warn("Review id: {} not found.", id);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", id));
        }
        return result.get();
    }

    public Collection<Review> findAllReviews(Long filmId, Long count) {
        if (filmId != null) {
            assertFilmExists(filmId);
        }
        return reviewStorage.findAllReviews(filmId, count);
    }

    public void addLikeToReview(Long id, Long userId) {
        assertReviewExists(id);
        assertUserExists(userId);
        assertReviewMarkNotExists(id, userId, true);
        Optional<ReviewMark> result = reviewStorage.addLikeToReview(id, userId);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not created.", id, userId, true);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not created.", id, userId, true));
        }
    }

    public void addDislikeToReview(Long id, Long userId) {
        assertReviewExists(id);
        assertUserExists(userId);
        assertReviewMarkNotExists(id, userId, false);
        Optional<ReviewMark> result = reviewStorage.addDislikeToReview(id, userId);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not created.", id, userId, false);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not created.", id, userId, false));
        }
    }

    public void deleteReviewLike(Long id, Long userId) {
        assertReviewMarkExists(id, userId, true);
        Optional<ReviewMark> result = reviewStorage.deleteReviewLike(id, userId);
        if (result.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not deleted.", id, userId, true);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not deleted.", id, userId, true));
        }
    }

    public void deleteReviewDislike(Long id, Long userId) {
        assertReviewMarkExists(id, userId, false);
        Optional<ReviewMark> result = reviewStorage.deleteReviewDislike(id, userId);
        if (result.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not deleted.", id, userId, false);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not deleted.", id, userId, false));
        }
    }

    private void assertReviewExists(Long reviewId) {
        Optional<Review> existedReview = reviewStorage.findReviewById(reviewId);
        if (existedReview.isEmpty()) {
            log.warn("Review id: {} not found.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", reviewId));
        }
    }

    private void assertFilmExists(Long id) {
        Optional<Film> existedFilm = filmStorage.findById(id);
        if (existedFilm.isEmpty()) {
            log.warn("Film id: {} not found.", id);
            throw new IncorrectObjectIdException(String.format("Film id: %d not found.", id));
        }
    }

    private void assertUserExists(Long id) {
        Optional<User> existedUser = userStorage.findById(id);
        if (existedUser.isEmpty()) {
            log.warn("User id: {} not found.", id);
            throw new IncorrectObjectIdException(String.format("User id: %d not found.", id));
        }
    }

    private void assertReviewMarkExists(Long reviewId, Long userId, boolean isPositive) {
        Optional<ReviewMark> existedMark = reviewStorage.findReviewMark(reviewId, userId, isPositive);
        if (existedMark.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} not found.", reviewId, userId, isPositive);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b not found.", reviewId, userId, isPositive)
            );
        }
    }

    private void assertReviewMarkNotExists(Long reviewId, Long userId, boolean isPositive) {
        Optional<ReviewMark> existedMark = reviewStorage.findReviewMark(reviewId, userId, isPositive);
        if (existedMark.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} already created.", reviewId, userId, isPositive);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Mark of review id: %d from user id: %d with value %b already created.",
                            reviewId, userId, isPositive));
        }
    }
}
