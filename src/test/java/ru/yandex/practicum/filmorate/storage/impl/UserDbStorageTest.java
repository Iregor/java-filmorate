package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    @Qualifier("userDb") private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM \"users\" ");
        jdbcTemplate.execute("ALTER TABLE \"users\" ALTER COLUMN \"user_id\" RESTART WITH 1 ");
    }

    @Test
    void findAll_return5Users_adding5User() {
        addData();
        Collection<User> collection = userStorage.findAll();
        assertThat(collection.size()).isEqualTo(5);
        assertThat(collection).asList().containsAnyOf(
                new User(1L, "email@yandex.ru", "trulala", "Trexo", "2011-03-08"),
                new User(2L, "ema@mail.ru", "login", "Name", "2001-06-05"),
                new User(3L, "ema@yahoo.ru", "loginator", "SurName", "1988-01-02"),
                new User(4L, "ail@rambler.ru", "user34321", "User", "2021-03-18"),
                new User(5L, "eml@ms.ru", "kpoisk", "Dbnjh", "1994-11-25"));
    }

    @Test
    void findById_returnUserId1_adding5User() {
        addData();
        assertThat(userStorage.findById(1L))
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "email@yandex.ru")
                                .hasFieldOrPropertyWithValue("login", "trulala")
                                .hasFieldOrPropertyWithValue("name", "Trexo")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2011-03-08"))
                );
    }

    @Test
    void create_returnNewUserId6_AllUser() {
        addData();
        User newUser = userStorage.create(new User("dsadsadsal@yandsadex.ru",
                "dsadsa", "dsadsa", "2001-01-12"));
        assertThat(newUser).hasFieldOrPropertyWithValue("id", 6L)
                .hasFieldOrPropertyWithValue("email", "dsadsadsal@yandsadex.ru")
                .hasFieldOrPropertyWithValue("login", "dsadsa")
                .hasFieldOrPropertyWithValue("name", "dsadsa")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2001-01-12"));
    }

   @Test
    void update_returnUpdateUserId4_AllUser() {
        addData();
        userStorage.update(new User(4L, "dsafd@fdsaf.ru",
               "fdasfd", "asdfas", "2000-01-12"));
        assertThat(userStorage.findById(4L))
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("email", "dsafd@fdsaf.ru")
                                .hasFieldOrPropertyWithValue("login", "fdasfd")
                                .hasFieldOrPropertyWithValue("name", "asdfas")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2000-01-12"))
                );

    }

    private void addData() {
        jdbcTemplate.update("INSERT INTO \"users\" (\"email\", \"login\", \"user_name\", \"birthday\" ) " +
                "VALUES ('email@yandex.ru', 'trulala', 'Trexo', '2011-03-08')," +
                "('ema@mail.ru', 'login', 'Name', '2001-06-05')," +
                "('ema@yahoo.ru', 'loginator', 'SurName', '1988-01-02')," +
                "('ail@rambler.ru', 'user34321', 'User', '2021-03-18')," +
                "('eml@ms.ru', 'kpoisk', 'Dbnjh', '1994-11-25')");

    }
}