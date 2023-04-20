package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody @Valid Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review findReviewById(@PathVariable Long reviewId) {
        return reviewService.findReviewById(reviewId);
    }

    @GetMapping
    public List<Review> findAllReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.findAllReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.addReviewMark(reviewId, userId, true);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.addReviewMark(reviewId, userId, false);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteReviewLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.deleteReviewMark(reviewId, userId, true);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.deleteReviewMark(reviewId, userId, false);
    }
}