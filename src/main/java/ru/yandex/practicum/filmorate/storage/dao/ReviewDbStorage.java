package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("reviewDB")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public Optional<Review> createReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        Long id = insert.executeAndReturnKey(getReviewParams(review)).longValue();
        return findReviewById(id);
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        jdbcTemplate.update(
                "UPDATE reviews` SET " +
                        "content = :content, " +
                        "is_positive = :is_positive " +
                        "WHERE review_id = :review_id; ",
                getReviewParams(review).addValue("review_id", review.getReviewId()));
        return findReviewById(review.getReviewId());
    }

    @Override
    public Optional<Review> deleteReview(Long reviewId) {
        jdbcTemplate.update(
                "DELETE FROM reviews WHERE review_id = :id",
                new MapSqlParameterSource().addValue("id", reviewId));
        return findReviewById(reviewId);
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM reviews " +
                            "LEFT OUTER JOIN " +
                            "(SELECT review_id, (SUM(is_like = true) - SUM(is_like = false)) as useful " +
                            "FROM review_marks " +
                            "WHERE review_id = :review_id " +
                            "GROUP BY review_id) AS review_marks " +
                            "ON reviews.review_id = review_marks.review_id " +
                            "WHERE reviews.review_id = :review_id; ",
                    new MapSqlParameterSource().addValue("review_id", reviewId),
                    this::mapReview));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> findAllReviews(Long filmId, Long count) {
        StringBuilder sqlQuery = new StringBuilder(
                "SELECT * FROM reviews " +
                        "LEFT OUTER JOIN " +
                        "(SELECT review_id, (SUM(is_like = true) - SUM(is_like = false)) AS useful " +
                        "FROM review_marks " +
                        "GROUP BY review_id) AS review_marks " +
                        "ON reviews.review_id = review_marks.review_id ");
        if (filmId != null) {
            sqlQuery.append("WHERE reviews.film_id = :filmId ");
        }
        sqlQuery.append(
                "ORDER BY review_marks.useful DESC " +
                        "LIMIT :count; ");
        try (Stream<Review> stream = jdbcTemplate.queryForStream(
                sqlQuery.toString(),
                new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("count", count),
                this::mapReview)) {
            return stream.sorted(Comparator.comparingLong(Review::getUseful).reversed()).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<ReviewMark> addLikeToReview(Long reviewId, Long userId) {
        new SimpleJdbcInsert(dataSource)
                .withTableName("review_marks")
                .execute(new MapSqlParameterSource()
                        .addValue("review_id", reviewId)
                        .addValue("user_id", userId)
                        .addValue("is_like", true));
        return findReviewMark(reviewId, userId, true);
    }

    @Override
    public Optional<ReviewMark> addDislikeToReview(Long reviewId, Long userId) {
        new SimpleJdbcInsert(dataSource)
                .withTableName("review_marks")
                .execute(new MapSqlParameterSource()
                        .addValue("review_id", reviewId)
                        .addValue("user_id", userId)
                        .addValue("is_like", false));
        return findReviewMark(reviewId, userId, false);
    }

    @Override
    public Optional<ReviewMark> deleteReviewLike(Long reviewId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM review_marks WHERE review_id = :id AND user_id = :userId AND is_like = true;",
                new MapSqlParameterSource().addValue("review_id", reviewId).addValue("user_id", userId));
        return findReviewMark(reviewId, userId, true);
    }

    @Override
    public Optional<ReviewMark> deleteReviewDislike(Long reviewId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM review_marks WHERE review_id = :id AND user_id = :userId AND is_like = false;",
                new MapSqlParameterSource().addValue("review_id", reviewId).addValue("user_id", userId));
        return findReviewMark(reviewId, userId, false);
    }

    @Override
    public Optional<ReviewMark> findReviewMark(Long reviewId, Long userId, boolean isLike) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM review_marks " +
                            "WHERE review_id = :reviewId AND user_id = :userId AND is_like = :isLike;",
                    new MapSqlParameterSource()
                            .addValue("reviewId", reviewId)
                            .addValue("userId", userId)
                            .addValue("isLike", isLike),
                    this::mapReviewMark
            ));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    private MapSqlParameterSource getReviewParams(Review review) {
        return new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId());
    }

    private Review mapReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("reviews.review_id"))
                .content(rs.getString("reviews.content"))
                .positive(rs.getBoolean("reviews.is_positive"))
                .userId(rs.getLong("reviews.user_id"))
                .filmId(rs.getLong("reviews.film_id"))
                .useful(rs.getLong("review_marks.useful"))
                .build();
    }

    private ReviewMark mapReviewMark(ResultSet rs, int rowNum) throws SQLException {
        return ReviewMark.builder()
                .reviewId(rs.getLong("review_id"))
                .userId(rs.getLong("user_id"))
                .isLike(rs.getBoolean("is_like"))
                .build();
    }
}
