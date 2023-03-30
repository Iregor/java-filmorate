INSERT INTO "rating_mpa"
VALUES (1, 'G'),
        (2, 'PG'),
        (3, 'PG-13'),
        (4, 'R'),
        (5, 'NC-17');

ALTER TABLE "rating_mpa"
    ALTER COLUMN "rating_id"
        RESTART WITH (SELECT MAX("rating_id") FROM "rating_mpa") + 1;

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


