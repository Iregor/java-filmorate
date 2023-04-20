package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    public Review createReview(Review review) {
        assertUserExists(review.getUserId());
        assertFilmExists(review.getFilmId());
        assertReviewNotExists(review.getUserId(), review.getFilmId());
        Optional<Review> result = reviewStorage.createReview(review);
        if (result.isEmpty()) {
            log.warn("Review of user: {} for film: {} is not created.", review.getUserId(), review.getFilmId());
            throw new IncorrectObjectIdException(String.format("Review of user %d for film %d is not created.",
                    review.getUserId(), review.getFilmId()));
        }
        Review rev = result.get();
        log.info("Добавление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());

        addFeed(rev.getUserId(), rev.getReviewId(), Operation.ADD);
        return result.get();
    }

    public Review updateReview(Review review) {
        assertUserExists(review.getUserId());
        assertFilmExists(review.getFilmId());
        assertReviewExists(review.getReviewId());
        log.info("Обновление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());

        addFeed(reviewStorage.findReviewById(review.getReviewId()).orElseThrow().getUserId(),
                review.getReviewId(),
                Operation.UPDATE);
        return reviewStorage.updateReview(review).orElseThrow();
    }

    public void deleteReview(Long reviewId) {
        assertReviewExists(reviewId);
        Review review = findReviewById(reviewId);
        Optional<Review> result = reviewStorage.deleteReview(reviewId);
        if (result.isPresent()) {
            log.warn("Review with id: {} is not deleted.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review with id: %d is not deleted.", reviewId));
        }

        log.info("Удаление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());
        addFeed(review.getUserId(), review.getReviewId(), Operation.REMOVE);
    }

    public Review findReviewById(Long reviewId) {
        Optional<Review> result = reviewStorage.findReviewById(reviewId);
        if (result.isEmpty()) {
            log.warn("Review id: {} not found.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", reviewId));
        }
        return result.get();
    }

    public List<Review> findAllReviews(Long filmId, Integer count) {
        if (filmId != null) {
            assertFilmExists(filmId);
            return reviewStorage.findAllReviewsByFilmId(filmId, count);
        }
        return reviewStorage.findAllReviews(count);
    }

    public void addReviewMark(Long reviewId, Long userId, Boolean isLike) {
        assertReviewExists(reviewId);
        assertUserExists(userId);
        assertReviewMarkNotExists(reviewId, userId, isLike);
        if (reviewStorage.findReviewMark(reviewId, userId, !(isLike)).isPresent()) {
            updateReviewMark(reviewId, userId, isLike);
            return;
        }
        Optional<ReviewMark> result = reviewStorage.createReviewMark(reviewId, userId, isLike);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not created.",
                    reviewId, userId, isLike);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not created.",
                    reviewId, userId, isLike));
        }
    }

    public void updateReviewMark(Long reviewId, Long userId, Boolean isLike) {
        assertReviewExists(reviewId);
        assertUserExists(userId);
        assertReviewMarkNotExists(reviewId, userId, isLike);
        Optional<ReviewMark> result = reviewStorage.updateReviewMark(reviewId, userId, isLike);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not updated.",
                    reviewId, userId, isLike);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is updates.",
                    reviewId, userId, isLike));
        }
    }

    public void deleteReviewMark(Long reviewId, Long userId, Boolean isLike) {
        assertReviewMarkExists(reviewId, userId, isLike);
        Optional<ReviewMark> result = reviewStorage.removeReviewMark(reviewId, userId, isLike);

        if (result.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not deleted.",
                    reviewId, userId, isLike);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not deleted.",
                    reviewId, userId, isLike));
        }
    }

    private void assertReviewExists(Long reviewId) {
        Optional<Review> existedReview = reviewStorage.findReviewById(reviewId);
        if (existedReview.isEmpty()) {
            log.warn("Review id: {} not found.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", reviewId));
        }
    }

    private void assertReviewNotExists(Long userId, Long filmId) {
        if (reviewStorage.isExistReview(userId, filmId)) {
            log.warn("Film {} already has a review from a user {}.", userId, filmId);
            throw new IncorrectObjectIdException(String.format("Film %d already has a review from a user %d.",
                    userId, filmId));
        }
    }

    private void assertFilmExists(Long filmId) {
        Optional<Film> existedFilm = filmStorage.findById(filmId);
        if (existedFilm.isEmpty()) {
            log.warn("Film id: {} not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film id: %d not found.", filmId));
        }
    }

    private void assertUserExists(Long userId) {
        Optional<User> existedUser = userStorage.findById(userId);
        if (existedUser.isEmpty()) {
            log.warn("User id: {} not found.", userId);
            throw new IncorrectObjectIdException(String.format("User id: %d not found.", userId));
        }
    }

    private void assertReviewMarkExists(Long reviewId, Long userId, Boolean isLike) {
        Optional<ReviewMark> existedMark = reviewStorage.findReviewMark(reviewId, userId, isLike);
        if (existedMark.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} not found.",
                    reviewId, userId, isLike);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b not found.",
                    reviewId, userId, isLike)
            );
        }
    }

    private void assertReviewMarkNotExists(Long reviewId, Long userId, Boolean isLike) {
        Optional<ReviewMark> existedMark = reviewStorage.findReviewMark(reviewId, userId, isLike);
        if (existedMark.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} already created.",
                    reviewId, userId, isLike);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Mark of review id: %d from user id: %d with value %b already created.",
                            reviewId, userId, isLike));
        }
    }

    private void addFeed(Long userId, Long reviewId, Operation operation) {
        eventService.addEvent(userId, reviewId, EventType.REVIEW, operation);
    }
}
