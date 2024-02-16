MERGE INTO RATING r
     USING VALUES (1) as d(rating_id)
        ON r.rating_id = d.rating_id
  WHEN NOT MATCHED THEN
INSERT (
	rating_id,
	rating_code,
	rating_name,
	rating_description)
VALUES (
     1,
    'G',
    'General Audiences',
    'All ages admitted. Nothing that would offend parents for viewing by children.'
);

MERGE INTO RATING r
     USING VALUES (2) as d(rating_id)
        ON r.rating_id = d.rating_id
  WHEN NOT MATCHED THEN
INSERT (
	rating_id,
	rating_code,
	rating_name,
	rating_description)
VALUES (
    2,
    'PG',
    'Parental Guidance Suggested',
    'Some material may not be suitable for children. Parents urged to give "parental guidance". May contain some material parents might not like for their young children.');

MERGE INTO RATING r
     USING VALUES (3) as d(rating_id)
        ON r.rating_id = d.rating_id
  WHEN NOT MATCHED THEN
INSERT (
	rating_id,
	rating_code,
	rating_name,
	rating_description)
VALUES (
    3,
    'PG-13',
    'Parents Strongly Cautioned',
    'Some material may be inappropriate for children under 13. Parents are urged to be cautious. Some material may be inappropriate for pre-teenagers.');

MERGE INTO RATING r
     USING VALUES (4) as d(rating_id)
        ON r.rating_id = d.rating_id
  WHEN NOT MATCHED THEN
INSERT (
	rating_id,
	rating_code,
	rating_name,
	rating_description)
VALUES (
    4,
    'R',
    'Restricted',
    'Under 17 requires accompanying parent or adult guardian. Contains some adult material. Parents are urged to learn more about the film before taking their young children with them.');

MERGE INTO RATING r
     USING VALUES (5) as d(rating_id)
        ON r.rating_id = d.rating_id
  WHEN NOT MATCHED THEN
INSERT (
	rating_id,
	rating_code,
	rating_name,
	rating_description)
VALUES (
    5,
    'NC-17',
    'Adults Only',
    'NO one 17 AND UNDER admitted. Clearly adult. Children ARE NOT admitted.'
);

MERGE INTO GENRE g
     USING VALUES (1) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (1, 'Комедия');

MERGE INTO GENRE g
     USING VALUES (2) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (2, 'Драма');

MERGE INTO GENRE g
     USING VALUES (3) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (3, 'Мультфильм');

MERGE INTO GENRE g
     USING VALUES (4) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (4, 'Триллер');

MERGE INTO GENRE g
     USING VALUES (5) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (5, 'Документальный');

MERGE INTO GENRE g
     USING VALUES (6) as d(genre_id)
        ON g.genre_id = d.genre_id
  WHEN NOT MATCHED THEN
INSERT (genre_id, genre_name)
	VALUES (6, 'Боевик');
