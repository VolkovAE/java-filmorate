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

--Заполнение тестовыми данными по пользователям
INSERT INTO User_ (email, login, name, birthday) VALUES ('va1@yandex.ru', 'Alex1', '--', '1981-02-27');
INSERT INTO User_ (email, login, name, birthday) VALUES ('werva1@yandex.ru', 'Lens1', '--', '2000-12-13');
INSERT INTO User_ (email, login, name, birthday) VALUES ('va1vb@yandex.ru', 'Alex12', '--', '1991-03-28');
INSERT INTO User_ (email, login, name, birthday) VALUES ('trewerva@yandex.ru', 'Lens12', '--', '2001-11-15');

--Заполняем тестовыми данными по фильмам.
INSERT INTO Film (name, rating, releaseDate, DURATION ) VALUES ('Trump 18', 1, '1977-04-29', 155);
INSERT INTO Film (name, rating, releaseDate, DURATION) VALUES ('Trump 2', 1, '1895-12-28', 105);
