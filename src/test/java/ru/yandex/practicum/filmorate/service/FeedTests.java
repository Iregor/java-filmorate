package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedTests {
    private final JdbcTemplate jdbcTemplate;
    private final FilmService filmService;
    private final EventService eventService;
    private final UserService userService;


    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM FEEDS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1 ");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1 ");
        jdbcTemplate.update("DELETE FROM FRIENDSHIPS");
        jdbcTemplate.update("DELETE FROM LIKES");

        addData();
    }

    @Test
    void getAFeedWithThreeEvents() {
        filmService.like(1L, 1L);
        filmService.like(2L, 1L);
        filmService.like(3L, 1L);

        Collection<Event> collection = eventService.getFeed(1L);
        List<Event> eventList = new ArrayList<>(collection);
        Event event = eventList.get(0);

        assertThat(eventService.getFeed(1L).size()).isEqualTo(3);
        assertThat(event.getEventType()).isEqualTo(EventType.LIKE);
        assertThat(event.getOperation()).isEqualTo(Operation.ADD);
    }

    @Test
    void getEmptyFeed() {
        assertThat(eventService.getFeed(1L).size()).isEqualTo(0);
    }

    @Test
    void getFeedWithDifferentEvents() {
        filmService.like(1L, 2L);
        filmService.like(1L, 1L);
        filmService.dislike(1L, 2L);

        filmService.like(2L, 2L);
        filmService.like(3L, 1L);

        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        userService.addFriend(1L, 4L);
        userService.deleteFriend(1L, 2L);

        Collection<Event> collection = eventService.getFeed(1L);
        List<Event> eventList = new ArrayList<>(collection);
        Event event = eventList.get(2);

        assertThat(eventService.getFeed(1L).size()).isEqualTo(6);
        assertThat(event.getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(event.getOperation()).isEqualTo(Operation.ADD);

        Event event_2 = eventList.get(4);
        Assertions.assertTrue(collection.contains(event_2));
    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO FILMS (RATING_ID, FILM_NAME, DESCRIPTION, " +
                "RELEASE_DATE, DURATION) " +
                "VALUES (1,'Евангелион 3.0+1.0', 'Мехи, гиганты и тд'," +
                " '2021-03-08', 155)," +
                "(5,'Карты, деньги, два ствола', 'Стейтем не бьет морды'," +
                " '1998-08-23', 107)," +
                "(2,'Большой куш', 'Борис Бритва вещает про надежность большого и тяжелого'," +
                " '2000-08-23', 104)," +
                "(3,'Побег из Шоушенка', 'Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены'," +
                " '1994-09-24', 142)," +
                "(4 ,'Аватар', 'Синие голые чуваки бегают по лесу'," +
                " '2009-12-10', 162);");

        jdbcTemplate.update("INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY ) " +
                "VALUES ('email@yandex.ru', 'trulala', 'Trexo', '2011-03-08')," +
                "('ema@mail.ru', 'login', 'Name', '2001-06-05')," +
                "('ema@yahoo.ru', 'loginator', 'SurName', '1988-01-02')," +
                "('ail@rambler.ru', 'user34321', 'User', '2021-03-18')," +
                "('eml@ms.ru', 'kpoisk', 'Dbnjh', '1994-11-25')");
    }
}
