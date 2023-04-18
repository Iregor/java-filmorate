package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewMark;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

@Repository("reviewDB")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    static final RowMapper<Review> reviewMapper =
            (rs, rowNum) -> Review.builder()
                    .reviewId(rs.getLong("REVIEW_ID"))
                    .content(rs.getString("CONTENT"))
                    .positive(rs.getBoolean("IS_POSITIVE"))
                    .userId(rs.getLong("USER_ID"))
                    .filmId(rs.getLong("FILM_ID"))
                    .useful(rs.getLong("USEFUL"))
                    .build();

    static final RowMapper<ReviewMark> reviewMarkMapper =
            (rs, rowNum) -> new ReviewMark(
                    rs.getLong("REVIEW_ID"),
                    rs.getLong("USER_ID"),
                    rs.getBoolean("IS_LIKE"));

    @Override
    public Optional<Review> createReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");
        Long id = insert
                .executeAndReturnKey(getReviewParams(review))
                .longValue();
        return findReviewById(id);
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        jdbcTemplate.update(
                "UPDATE REVIEWS SET " +
                        "CONTENT = :CONTENT, " +
                        "IS_POSITIVE = :IS_POSITIVE " +
                        "WHERE REVIEW_ID = :REVIEW_ID;",
                getReviewParams(review));
        return findReviewById(review.getReviewId());
    }

    @Override
    public Optional<Review> deleteReview(Long reviewId) {
        jdbcTemplate.update(
                "DELETE FROM REVIEWS " +
                        "WHERE REVIEW_ID = :REVIEW_ID",
                new MapSqlParameterSource()
                        .addValue("REVIEW_ID", reviewId));
        return findReviewById(reviewId);
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT R.REVIEW_ID," +
                            "CONTENT," +
                            "IS_POSITIVE," +
                            "R.USER_ID," +
                            "FILM_ID, " +
                            "SUM(RM.IS_LIKE = TRUE) - SUM(RM.IS_LIKE = FALSE) USEFUL " +
                            "FROM REVIEWS R " +
                            "LEFT OUTER JOIN REVIEW_MARKS RM ON R.REVIEW_ID = RM.REVIEW_ID " +
                            "WHERE R.REVIEW_ID = :REVIEW_ID " +
                            "GROUP BY R.REVIEW_ID;",
                    new MapSqlParameterSource().addValue("REVIEW_ID", reviewId),
                    reviewMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Collection<Review> findAllReviewsByFilmId(Long filmId, Integer count) {
        return jdbcTemplate.query(
                "SELECT R.REVIEW_ID," +
                        "CONTENT," +
                        "IS_POSITIVE," +
                        "R.USER_ID," +
                        "FILM_ID, " +
                        "COALESCE(SUM(RM.IS_LIKE = TRUE) - SUM(RM.IS_LIKE = FALSE), 0) USEFUL " +
                        "FROM REVIEWS R " +
                        "LEFT OUTER JOIN REVIEW_MARKS RM ON R.REVIEW_ID = RM.REVIEW_ID " +
                        "WHERE R.FILM_ID = :FILM_ID " +
                        "GROUP BY R.REVIEW_ID " +
                        "ORDER BY USEFUL DESC " +
                        "LIMIT :COUNT;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("COUNT", count),
                reviewMapper);
    }

    @Override
    public Collection<Review> findAllReviews(Integer count) {
        return jdbcTemplate.query(
                "SELECT R.REVIEW_ID," +
                        "CONTENT," +
                        "IS_POSITIVE," +
                        "R.USER_ID," +
                        "FILM_ID, " +
                        "COALESCE(SUM(RM.IS_LIKE = TRUE) - SUM(RM.IS_LIKE = FALSE), 0) USEFUL " +
                        "FROM REVIEWS R " +
                        "LEFT OUTER JOIN REVIEW_MARKS RM ON R.REVIEW_ID = RM.REVIEW_ID " +
                        "GROUP BY R.REVIEW_ID " +
                        "ORDER BY USEFUL DESC " +
                        "LIMIT :COUNT;",
                new MapSqlParameterSource()
                        .addValue("COUNT", count),
                reviewMapper);
    }

    @Override
    public Optional<ReviewMark> createReviewMark(Long reviewId, Long userId, Boolean isLike) {
        new SimpleJdbcInsert(dataSource)
                .withTableName("REVIEW_MARKS")
                .execute(getReviewMarkParams(new ReviewMark(reviewId, userId, isLike)));
        return findReviewMark(reviewId, userId, isLike);
    }

    @Override
    public Optional<ReviewMark> removeReviewMark(Long reviewId, Long userId, Boolean isLike) {
        jdbcTemplate.update(
                "DELETE FROM REVIEW_MARKS " +
                        "WHERE REVIEW_ID = :REVIEW_ID " +
                        "AND USER_ID = :USER_ID " +
                        "AND IS_LIKE = :IS_LIKE;",
                getReviewMarkParams(new ReviewMark(reviewId, userId, isLike)));
        return findReviewMark(reviewId, userId, true);
    }

    @Override
    public Optional<ReviewMark> findReviewMark(Long reviewId, Long userId, Boolean isLike) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM REVIEW_MARKS " +
                            "WHERE REVIEW_ID = :REVIEW_ID " +
                            "AND USER_ID = :USER_ID " +
                            "AND IS_LIKE = :IS_LIKE;",
                    getReviewMarkParams(new ReviewMark(reviewId, userId, isLike)),
                    reviewMarkMapper
            ));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    private MapSqlParameterSource getReviewParams(Review review) {
        return new MapSqlParameterSource()
                .addValue("REVIEW_ID", review.getReviewId())
                .addValue("CONTENT", review.getContent())
                .addValue("IS_POSITIVE", review.getPositive())
                .addValue("USER_ID", review.getUserId())
                .addValue("FILM_ID", review.getFilmId());
    }

    private MapSqlParameterSource getReviewMarkParams(ReviewMark reviewMark) {
        return new MapSqlParameterSource()
                .addValue("REVIEW_ID", reviewMark.getReviewId())
                .addValue("USER_ID", reviewMark.getUserId())
                .addValue("IS_LIKE", reviewMark.isLike());
    }
}
