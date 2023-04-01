package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage.genreMapper;
import static ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage.genresExtractor;

@Repository("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    static final RowMapper<Film> filmMapper =
            ((rs, rowNum) -> Film.builder()
                    .id(rs.getLong("FILM_ID"))
                    .name(rs.getString("FILM_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .rate(rs.getInt("RATE"))
                    .mpa(new Mpa(rs.getLong("RATING_ID"),
                            rs.getString("RATING_NAME")))
                    .genres(new HashSet<>())
                    .likes(new HashSet<>())
                    .build());

    @Override
    public Collection<Film> findAll() {
        Collection<Film> result = jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "ORDER BY F.FILM_ID; ",
                filmMapper);
        findFilmData(result);
        return result;
    }

    @Override
    public Collection<Film> findPopularFilms(int size) {
        Collection<Film> result = jdbcTemplate.query(
                "SELECT * FROM FILMS F " +
                        "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                        "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                        "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                        "ORDER BY R.RATE DESC " +
                        "LIMIT :SIZE;",
                new MapSqlParameterSource()
                        .addValue("SIZE", size),
                filmMapper);
        findFilmData(result);
        return result;
    }

    @Override
    public Collection<Film> findFilmsByParams(String name, LocalDate after, LocalDate before) {
        return null;
    }

    @Override
    public Optional<Film> findById(Long id) {
        try {
            Optional<Film> result = Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM FILMS F " +
                            "JOIN RATING MPA ON F.RATING_ID = MPA.RATING_ID " +
                            "LEFT OUTER JOIN (SELECT FILM_ID, COUNT(USER_ID) RATE FROM LIKES " +
                            "GROUP BY FILM_ID) R ON R.FILM_ID = F.FILM_ID " +
                            "WHERE F.FILM_ID = :FILM_ID;",
                    new MapSqlParameterSource()
                            .addValue("FILM_ID", id),
                    filmMapper));
            result.ifPresent(f -> findFilmData(List.of(f)));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        long id = insert
                .executeAndReturnKey(getFilmParams(film))
                .longValue();
        Optional<Film> result = findById(id);
        result.ifPresent(f -> {
            film.getGenres().forEach(genre -> addFilmGenres(f.getId(), genre.getId()));
            findFilmData(List.of(f));
        });
        return result;
    }


    @Override
    public Optional<Film> update(Film film) {
        jdbcTemplate.update(
                "UPDATE FILMS " +
                        "SET FILM_NAME = :FILM_NAME, DESCRIPTION = :DESCRIPTION," +
                        "RELEASE_DATE = :RELEASE_DATE, DURATION = :DURATION, " +
                        "RATING_ID = :RATING_ID " +
                        "WHERE FILM_ID = :FILM_ID; ",
                getFilmParams(film));
        Optional<Film> result = findById(film.getId());
        result.ifPresent(f -> {
            Set<Genre> removedGenre = findByFilmId(film.getId())
                    .stream()
                    .filter(genre -> !film.getGenres()
                            .contains(genre)).collect(Collectors.toSet());

            Set<Genre> addedGenre = film.getGenres()
                    .stream()
                    .filter(genre -> !findByFilmId(film.getId())
                            .contains(genre)).collect(Collectors.toSet());

            removedGenre.forEach(genre -> deleteFilmGenres(film.getId(), genre.getId()));
            addedGenre.forEach(genre -> addFilmGenres(film.getId(), genre.getId()));

            findFilmData(List.of(result.get()));
        });
        return result;
    }

    public Set<Genre> findByFilmId(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(
                "SELECT G.GENRE_ID, G.GENRE_NAME " +
                        "FROM GENRES G " +
                        "JOIN FILM_GENRES FG ON G.GENRE_ID = FG.GENRE_ID " +
                        "WHERE FG.FILM_ID = :FILM_ID " +
                        "ORDER BY G.GENRE_ID;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId),
                genreMapper));
    }

    public void deleteFilmGenres(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRES " +
                        "WHERE FILM_ID = :FILM_ID AND GENRE_ID = :GENRE_ID;",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("GENRE_ID", genreId));
    }

    public void addFilmGenres(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "INSERT INTO FILM_GENRES VALUES (:FILM_ID,:GENRE_ID);",
                new MapSqlParameterSource()
                        .addValue("FILM_ID", filmId)
                        .addValue("GENRE_ID", genreId));
    }

    private void findFilmData(Collection<Film> films) {
        Map<Long, Film> filmsMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        SqlParameterSource ids = new MapSqlParameterSource("IDS", filmsMap.keySet());

        Map<Long, Set<Genre>> genresMap = jdbcTemplate.query(
                "SELECT * FROM FILM_GENRES FG " +
                        "JOIN GENRES G on G.GENRE_ID = FG.GENRE_ID " +
                        "WHERE FILM_ID IN (:IDS) " +
                        "ORDER BY G.GENRE_ID",
                ids,
                genresExtractor);

        films.forEach(film -> {
            film.setGenres(new HashSet<>());
            if (Objects.requireNonNull(genresMap).containsKey(film.getId())) {
                film.setGenres(genresMap.get(film.getId()));
            }
        });

        jdbcTemplate.query(
                "SELECT * FROM LIKES " +
                        "WHERE USER_ID IN (:IDS) " +
                        "ORDER BY FILM_ID;",
                ids,
                (rs, rowNum) -> filmsMap
                        .get(rs.getLong("FILM_ID"))
                        .getLikes()
                        .add(rs.getLong("USER_ID")));
    }

    private MapSqlParameterSource getFilmParams(Film film) {
        return new MapSqlParameterSource()
                .addValue("FILM_ID", film.getId())
                .addValue("FILM_NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("RATING_ID", film.getMpa().getId());
    }
}

