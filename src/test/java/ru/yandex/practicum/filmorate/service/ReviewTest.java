package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewService reviewService;

    private Film film1, film2, film3;
    private User user1, user2, user3;
    private Review rev1, rev2, rev3, rev4;
    private Long user1Id, user2Id;
    private Long rev1Id, rev2Id, rev3Id;

    @BeforeEach
    void createInitialData() {
        createEntities();
        film1 = filmService.create(film1);
        film2 = filmService.create(film2);
        film3 = filmService.create(film3);
        user1 = userService.create(user1);
        user1Id = user1.getId();
        user2 = userService.create(user2);
        user2Id = user2.getId();
        user3 = userService.create(user3);
        rev1 = reviewService.createReview(rev1);
        rev1Id = rev1.getReviewId();
        rev2 = reviewService.createReview(rev2);
        rev2Id = rev2.getReviewId();
        rev3 = reviewService.createReview(rev3);
        rev3Id = rev3.getReviewId();
    }

    @AfterEach
    void cleanAllData() {
        jdbcTemplate.update("DELETE FROM LIKES;");
        jdbcTemplate.update("DELETE FROM FILM_GENRES;");
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS;");
        jdbcTemplate.update("DELETE FROM USERS;");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM DIRECTORS;");
        jdbcTemplate.execute("ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM FILMS;");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM REVIEWS;");
        jdbcTemplate.execute("ALTER TABLE REVIEWS ALTER COLUMN REVIEW_ID RESTART WITH 1;");
    }

    @Test
    void createReview_return4Reviews_added3Reviews() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        reviewService.createReview(rev4);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(4);
    }

    @Test
    void updateReview_returnUpdatedReview_added3Reviews() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);

        String updatedContent = "Обязательно посмотреть!";
        rev3 = reviewService.findReviewById(rev3Id);
        rev3.setContent(updatedContent);
        reviewService.updateReview(rev3);

        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        assertThat(reviewService.findReviewById(rev3Id).getContent()).isEqualTo(updatedContent);
    }

    @Test
    void deleteReview_return0Reviews_added3Reviews() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        reviewService.deleteReview(rev1Id);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(2);
        reviewService.deleteReview(rev2Id);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(1);
        reviewService.deleteReview(rev3Id);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(0);
    }

    @Test
    void deleteReview_throwIncorrectObjectIdException_deletedReview() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        reviewService.deleteReview(rev1Id);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(2);
        assertThatThrownBy(() -> reviewService.deleteReview(rev1Id)).isInstanceOf(IncorrectObjectIdException.class);
    }

    @Test
    void deleteReview_return2Reviews_added3ReviewsWithMarks() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        reviewService.addReviewMark(rev1Id, user2Id, false);
        reviewService.deleteReview(rev1Id);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(2);
    }

    @Test
    void findReviewById_returnReviewsWithRightId_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getContent()).isEqualTo(rev1.getContent());
        assertThat(reviewService.findReviewById(rev2Id).getContent()).isEqualTo(rev2.getContent());
        assertThat(reviewService.findReviewById(rev3Id).getContent()).isEqualTo(rev3.getContent());
    }

    @Test
    void findAllReviews_return3Reviews_added3Reviews() {
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(3);
        reviewService.deleteReview(1L);
        reviewService.deleteReview(2L);
        reviewService.deleteReview(3L);
        assertThat(reviewService.findAllReviews(null, 10).size()).isEqualTo(0);
    }

    @Test
    void findAllReviewsOrderTest() {
        assertThat(new ArrayList<>(reviewService.findAllReviews(null, 10))
                .get(0).getContent()).isEqualTo(rev1.getContent());
        reviewService.addReviewMark(rev2Id, user1Id, true);
        assertThat(new ArrayList<>(reviewService.findAllReviews(null, 10))
                .get(0).getContent()).isEqualTo(rev2.getContent());
        reviewService.addReviewMark(rev3Id, user1Id, true);
        reviewService.addReviewMark(rev3Id, user2Id, true);
        assertThat(new ArrayList<>(reviewService.findAllReviews(null, 10))
                .get(0).getContent()).isEqualTo(rev3.getContent());
        reviewService.deleteReviewMark(rev3Id, user2Id, true);
        assertThat(new ArrayList<>(reviewService.findAllReviews(null, 10))
                .get(0).getContent()).isEqualTo(rev2.getContent());
        reviewService.deleteReviewMark(rev3Id, user1Id, true);
        reviewService.deleteReviewMark(rev2Id, user1Id, true);
        assertThat(new ArrayList<>(reviewService.findAllReviews(null, 10))
                .get(0).getContent()).isEqualTo(rev1.getContent());
    }

    @Test
    void addLikeToReview_returnReviewsWithLikes_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        reviewService.addReviewMark(rev1Id, user2Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(2);
    }

    @Test
    void addMarksToReview_returnReviewsWithUpdatedMarks_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        reviewService.addReviewMark(rev1Id, user1Id, false);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(-1);
    }

    @Test
    void addLikeToReview_throwResponseStatusException_addedDuplicateLike() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        assertThatThrownBy(() ->
                reviewService.addReviewMark(rev1Id, user1Id, true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void  addDisLikeToReview_returnReviewsWithDisLikes_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        reviewService.addReviewMark(rev1Id, user2Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(2);
    }

    @Test
    void addDisLikeToReview_throwResponseStatusException_addedDuplicateDisLike() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        assertThatThrownBy(() ->
                reviewService.addReviewMark(rev1Id, user1Id, true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteLikeToReview_returnReviewsWithOutLikes_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(1);
        reviewService.deleteReviewMark(rev1Id, user1Id, true);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
    }

    @Test
    void deleteDisLikeToReview_returnReviewsWithOutDisLikes_added3Reviews() {
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
        reviewService.addReviewMark(rev1Id, user1Id, false);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(-1);
        reviewService.deleteReviewMark(rev1Id, user1Id, false);
        assertThat(reviewService.findReviewById(rev1Id).getUseful()).isEqualTo(0);
    }


    private void createEntities() {
        film1 = Film.builder()
                .name("Гладиатор")
                .description("Исторический художественный фильм режиссёра Ридли Скотта")
                .releaseDate(LocalDate.of(2000, 5, 1))
                .duration(155)
                .mpa(new Mpa(4L, "R"))
                .genres(Set.of(new Genre(2L, "Драма"), new Genre(6L, "Боевик")))
                .directors(new HashSet<>())
                .build();
        film2 = Film.builder()
                .name("Властелин колец: Братство Кольца")
                .description("Power can be held in the smallest of things...")
                .releaseDate(LocalDate.of(2001, 12, 10))
                .duration(178)
                .mpa(new Mpa(3L, "PG-13"))
                .genres(Set.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")))
                .directors(new HashSet<>())
                .build();
        film3 = Film.builder()
                .name("Служебный Роман")
                .description("Комедия Эльдара Рязанова, классика советского кино")
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(159)
                .mpa(new Mpa(1L, "G"))
                .genres(Set.of(new Genre(1L, "Комедия"), new Genre(5L, "Документальный")))
                .directors(new HashSet<>())
                .build();

        user1 = User.builder()
                .email("anton@yandex.ru")
                .login("Anton")
                .name("Антон")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        user2 = User.builder()
                .email("dasha@yandex.ru")
                .login("Dasha")
                .name("Дарья")
                .birthday(LocalDate.of(1995, 2, 10))
                .build();
        user3 = User.builder()
                .email("ivan@yandex.ru")
                .login("Ivan")
                .name("Иван")
                .birthday(LocalDate.of(2000, 5, 25))
                .build();
        rev1 = Review.builder()
                .content("Вполне неплохой фильм, но на один раз.")
                .filmId(1L)
                .userId(1L)
                .positive(true)
                .build();
        rev2 = Review.builder()
                .content("Фильм не понравился... скучный")
                .filmId(2L)
                .userId(2L)
                .positive(false)
                .build();
        rev3 = Review.builder()
                .content("Прекрасная комедия, всем надо посмотреть!")
                .filmId(3L)
                .userId(3L)
                .positive(true)
                .build();
        rev4 = Review.builder()
                .content("Думаю не посоветую на него идти.")
                .userId(3L)
                .filmId(1L)
                .positive(false)
                .build();
    }
}
