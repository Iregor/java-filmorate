INSERT INTO PUBLIC.RATING (RATING_NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO "genres"
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

ALTER TABLE "genres"
    ALTER COLUMN "genre_id"
        RESTART WITH (SELECT MAX("genre_id") FROM "genres") + 1;


