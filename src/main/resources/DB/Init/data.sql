;   --одна команда должны быть, если не будет запросов

--Заполнение таблицы Rating предопределенными значениями: G, PG, PG-13, R, NC-17
INSERT INTO Rating (name)
SELECT 'G'
UNION
SELECT 'PG'
UNION
SELECT 'PG-13'
UNION
SELECT 'R'
UNION
SELECT 'NC-17'
EXCEPT
SELECT name
FROM Rating;

--Заполнение таблицы Genre предопределенными значениями: Драма, Детектив, Комедия, Триллер, Фантастика
INSERT INTO Genre (name)
SELECT 'Драма'
UNION
SELECT 'Детектив'
UNION
SELECT 'Комедия'
UNION
SELECT 'Триллер'
UNION
SELECT 'Фантастика'
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
