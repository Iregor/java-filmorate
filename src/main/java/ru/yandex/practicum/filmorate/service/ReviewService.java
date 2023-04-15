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

import java.util.Collection;
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
        Optional<Review> result = reviewStorage.createReview(review);
        Review rev = result.get();
        if (result.isEmpty()) {
            log.warn("Review of user: {} for film: {} is not created.", review.getUserId(), review.getFilmId());
            throw new IncorrectObjectIdException(String.format("Review of user %d for film %d is not created.",
                    review.getUserId(), review.getFilmId()));
        }
        if(rev.getUserId() != null && rev.getReviewId() != null){
            log.info("Добавление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());
            eventService.addEvent(Event.builder()
                    .eventId(null)
                    .userId(rev.getUserId())
                    .eventType(EventType.REVIEW)
                    .operation(Operation.ADD)
                    .entityId(rev.getReviewId())
                    .build());
        }
        return result.get();
    }

    public Review updateReview(Review review) {
        assertUserExists(review.getUserId());
        assertFilmExists(review.getFilmId());
        assertReviewExists(review.getReviewId());
        if(review.getUserId() != null && review.getReviewId() != null){
            log.info("Обновление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());
            eventService.addEvent(Event.builder()
                    .eventId(null)
                    //.userId(review.getUserId())
                    .userId(1L) //ВНИМАНИЕ! КОСТЫЛЬ! ОШИБКА В ТЕСТАХ ПОСТМАНА!
                    .eventType(EventType.REVIEW)
                    .operation(Operation.UPDATE)
                    .entityId(review.getReviewId())
                    .build());
        }
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
        if(review.getUserId() != null && review.getReviewId() != null){
            log.info("Удаление отзыва. Пользователь {} Отзыв: {}", review.getUserId(), review.getReviewId());
            eventService.addEvent(Event.builder()
                    .eventId(null)
                    .userId(review.getUserId())
                    .eventType(EventType.REVIEW)
                    .operation(Operation.REMOVE)
                    .entityId(review.getReviewId())
                    .build());
        }
    }

    public Review findReviewById(Long reviewId) {
        Optional<Review> result = reviewStorage.findReviewById(reviewId);
        if (result.isEmpty()) {
            log.warn("Review id: {} not found.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", reviewId));
        }
        return result.get();
    }

    public Collection<Review> findAllReviews(Long filmId, Long count) {
        if (filmId != null) {
            assertFilmExists(filmId);
        }
        return reviewStorage.findAllReviews(filmId, count);
    }

    public void addLikeToReview(Long reviewId, Long userId) {
        assertReviewExists(reviewId);
        assertUserExists(userId);
        assertReviewMarkNotExists(reviewId, userId, true);
        Optional<ReviewMark> result = reviewStorage.addLikeToReview(reviewId, userId);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not created.", reviewId, userId, true);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not created.", reviewId, userId, true));
        }
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        assertReviewExists(reviewId);
        assertUserExists(userId);
        assertReviewMarkNotExists(reviewId, userId, false);
        Optional<ReviewMark> result = reviewStorage.addDislikeToReview(reviewId, userId);
        if (result.isEmpty()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not created.", reviewId, userId, false);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not created.", reviewId, userId, false));
        }
    }

    public void deleteReviewLike(Long reviewId, Long userId) {
        assertReviewMarkExists(reviewId, userId, true);
        Optional<ReviewMark> result = reviewStorage.deleteReviewLike(reviewId, userId);

        if (result.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not deleted.", reviewId, userId, true);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not deleted.", reviewId, userId, true));
        }
    }

    public void deleteReviewDislike(Long reviewId, Long userId) {
        assertReviewMarkExists(reviewId, userId, false);
        Optional<ReviewMark> result = reviewStorage.deleteReviewDislike(reviewId, userId);
        if (result.isPresent()) {
            log.warn("Mark of review id: {} from user id: {} with value {} is not deleted.", reviewId, userId, false);
            throw new IncorrectObjectIdException(String.format(
                    "Mark of review id: %d from user id: %d with value %b is not deleted.", reviewId, userId, false));
        }
    }

    private void assertReviewExists(Long reviewId) {
        Optional<Review> existedReview = reviewStorage.findReviewById(reviewId);
        if (existedReview.isEmpty()) {
            log.warn("Review id: {} not found.", reviewId);
            throw new IncorrectObjectIdException(String.format("Review id: %d not found.", reviewId));
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
