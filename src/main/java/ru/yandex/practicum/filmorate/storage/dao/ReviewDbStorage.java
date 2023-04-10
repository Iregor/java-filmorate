package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

@Repository("reviewDB")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public Optional<Review> createReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");
        Long id = insert.executeAndReturnKey(getParaSource(review)).longValue();

        return null;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> deleteReview(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> findReviewById(Long userId) {
        return Optional.empty();
    }

    @Override
    public Collection<Review> findAllReviews(Long filmId, Long count) {
        return null;
    }

    @Override
    public void addLikeToReview(Long id, Long userId) {

    }

    @Override
    public void addDislikeToReview(Long id, Long userId) {

    }

    @Override
    public void deleteReviewLike(Long id, Long userId) {

    }

    @Override
    public void deleteReviewDislike(Long id, Long userId) {

    }

    private MapSqlParameterSource getParaSource(Review review) {
        return null;
    }
}
