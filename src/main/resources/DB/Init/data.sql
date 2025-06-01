;   --одна команда должны быть, если не будет запросов

--Заполнение таблицы Rating предопределенными значениями: G, PG, PG-13, R, NC-17
--INSERT INTO Rating (name)
--SELECT 'G'
--UNION
--SELECT 'PG'
--UNION
--SELECT 'PG-13'
--UNION
--SELECT 'R'
--UNION
--SELECT 'NC-17'
--EXCEPT
--SELECT name
--FROM Rating;

-- под тесты порядок изменен
INSERT INTO Rating (name)
SELECT 'G'
EXCEPT
SELECT name
FROM Rating;

INSERT INTO Rating (name)
SELECT 'PG'
EXCEPT
SELECT name
FROM Rating;

INSERT INTO Rating (name)
SELECT 'PG-13'
EXCEPT
SELECT name
FROM Rating;

INSERT INTO Rating (name)
SELECT 'R'
EXCEPT
SELECT name
FROM Rating;

INSERT INTO Rating (name)
SELECT 'NC-17'
EXCEPT
SELECT name
FROM Rating;

--Заполнение таблицы Genre предопределенными значениями: Драма, Детектив, Комедия, Триллер, Фантастика
--INSERT INTO Genre (name)
--SELECT 'Комедия'
--UNION
--SELECT 'Драма'
--UNION
--SELECT 'Мультфильм'
--UNION
--SELECT 'Триллер'
--UNION
--SELECT 'Документальный'
--UNION
--SELECT 'Боевик'
--EXCEPT
--SELECT name
--FROM Genre;

-- под тесты порядок изменен
INSERT INTO Genre (name)
SELECT 'Комедия'
EXCEPT
SELECT name
FROM Genre;

INSERT INTO Genre (name)
SELECT 'Драма'
EXCEPT
SELECT name
FROM Genre;

INSERT INTO Genre (name)
SELECT 'Мультфильм'
EXCEPT
SELECT name
FROM Genre;

INSERT INTO Genre (name)
SELECT 'Триллер'
EXCEPT
SELECT name
FROM Genre;

INSERT INTO Genre (name)
SELECT 'Документальный'
EXCEPT
SELECT name
FROM Genre;

INSERT INTO Genre (name)
SELECT 'Боевик'
EXCEPT
SELECT name
FROM Genre;

--Заполнение таблицы StatusFriendship предопределенными значениями: Confirm, Not confirm
INSERT INTO StatusFriendship (name)
SELECT 'Confirm'
UNION
SELECT 'Not confirm'
EXCEPT
SELECT name
FROM StatusFriendship;
