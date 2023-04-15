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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmService fs;
    private final UserService us;
    private final ReviewService rs;

    private Film film1, film2, film3;
    private User user1, user2, user3;
    private Review rev1, rev2, rev3, rev4;
    private Long film1Id, film2Id, film3Id;
    private Long user1Id, user2Id, user3Id;
    private Long rev1Id, rev2Id, rev3Id;

    @BeforeEach
    void createInitialData() {
        createEntities();
        film1 = fs.create(film1);
        film1Id = film1.getId();
        film2 = fs.create(film2);
        film2Id = film2.getId();
        film3 = fs.create(film3);
        film3Id = film3.getId();
        user1 = us.create(user1);
        user1Id = user1.getId();
        user2 = us.create(user2);
        user2Id = user2.getId();
        user3 = us.create(user3);
        user3Id = user3.getId();
        rev1 = rs.createReview(rev1);
        rev1Id = rev1.getReviewId();
        rev2 = rs.createReview(rev2);
        rev2Id = rev2.getReviewId();
        rev3 = rs.createReview(rev3);
        rev3Id = rev3.getReviewId();
    }

    @AfterEach
    void cleanAllData() {
        jdbcTemplate.update("DELETE FROM LIKES ");
        jdbcTemplate.update("DELETE FROM FILM_GENRES ");
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS");
        jdbcTemplate.update("DELETE FROM USERS ");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
        jdbcTemplate.update("DELETE FROM directors");
        jdbcTemplate.execute("ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM FILMS ");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");
        jdbcTemplate.update("DELETE FROM REVIEWS ");
        jdbcTemplate.execute("ALTER TABLE REVIEWS ALTER COLUMN REVIEW_ID RESTART WITH 1 ");
    }

    @Test
    void createReviewTest() {
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);
        rs.createReview(rev4);
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(4);
    }

    @Test
    void updateReviewTest() {
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);

        String updatedContent = "Обязательно посмотреть!";
        rev3 = rs.findReviewById(rev3Id);
        rev3.setContent(updatedContent);
        rs.updateReview(rev3);

        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);
        assertThat(rs.findReviewById(rev3Id).getContent().equals(updatedContent));
    }

    @Test
    void deleteReviewTest() {
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);
        rs.deleteReview(rev1Id);
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(2);
        rs.deleteReview(rev2Id);
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(1);
        rs.deleteReview(rev3Id);
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(0);
    }

    @Test
    void deleteRepeatableReviewTest() {
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);
        rs.deleteReview(rev1Id);
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(2);
        assertThatThrownBy(() -> rs.deleteReview(rev1Id)).isInstanceOf(IncorrectObjectIdException.class);
    }

    @Test
    void findReviewById() {
        assertThat(rs.findReviewById(rev1Id).getContent().equals(rev1.getContent()));
        assertThat(rs.findReviewById(rev2Id).getContent().equals(rev1.getContent()));
        assertThat(rs.findReviewById(rev3Id).getContent().equals(rev1.getContent()));
    }

    @Test
    void findAllReviewsTest() {
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(3);
        jdbcTemplate.update("DELETE FROM reviews; ");
        assertThat(rs.findAllReviews(null, 10L).size()).isEqualTo(0);
    }

    @Test
    void findAllReviewsOrderTest() {
        assertThat(List.of(rs.findAllReviews(null, 10L)).get(0).equals(rev1.getContent()));
        rs.addLikeToReview(rev2Id, user1Id);
        assertThat(List.of(rs.findAllReviews(null, 10L)).get(0).equals(rev2.getContent()));
        rs.addLikeToReview(rev3Id, user1Id);
        rs.addLikeToReview(rev3Id, user2Id);
        assertThat(List.of(rs.findAllReviews(null, 10L)).get(0).equals(rev3.getContent()));
        rs.deleteReviewLike(rev3Id, user2Id);
        assertThat(List.of(rs.findAllReviews(null, 10L)).get(0).equals(rev2.getContent()));
        rs.deleteReviewLike(rev3Id, user1Id);
        rs.deleteReviewLike(rev2Id, user1Id);
        assertThat(List.of(rs.findAllReviews(null, 10L)).get(0).equals(rev1.getContent()));
    }

    @Test
    void addLikeToReviewTest() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addLikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(1L));
        rs.addLikeToReview(rev1Id, user2Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(2L));
    }

    @Test
    void addDuplicateLikeToReviewTest() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addLikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(1L));
        assertThatThrownBy(() -> rs.addLikeToReview(rev1Id, user1Id)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void addDisLikeToReviewTest() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addLikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(-1L));
        rs.addLikeToReview(rev1Id, user2Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(-2L));
    }

    @Test
    void addDuplicateDislikeToReviewTest() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addLikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(1L));
        assertThatThrownBy(() -> rs.addLikeToReview(rev1Id, user1Id)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteReviewLikeTest() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addLikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(1L));
        rs.deleteReviewLike(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
    }

    @Test
    void deleteReviewDislike() {
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
        rs.addDislikeToReview(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(-1L));
        rs.deleteReviewDislike(rev1Id, user1Id);
        assertThat(rs.findReviewById(rev1Id).getUseful().equals(0L));
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
                .userId(1L)
                .filmId(1L)
                .positive(false)
                .build();
    }
}
