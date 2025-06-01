package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.converters.DurationToIntegerConverter;
import ru.yandex.practicum.filmorate.converters.IntegerToDurationConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.mpa.Mpa;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.extractors.FriendsResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.rating.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class,
        UserRowMapper.class,
        StringToInstantConverter.class,
        FriendsResultSetExtractor.class,
        FilmDbStorage.class,
        FilmRowMapper.class,
        MpaDbStorage.class,
        MpaRowMapper.class,
        IntegerToDurationConverter.class,
        DurationToIntegerConverter.class,
        GenreDbStorage.class,
        GenreRowMapper.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/DB/Init/data_init.sql")
public class FilmorateApplicationTestsDBStorage {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final StringToInstantConverter stringToInstantConverter;
    private final IntegerToDurationConverter integerToDurationConverter;

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setEmail("vbrt@ya.ru");
        user.setLogin("Mariy");
        user.setName("");
        user.setBirthday(stringToInstantConverter.convert("1977-04-29"));

        User userAdd = userStorage.add(user);

        assertNotEquals(0L, userAdd.getId().longValue(),
                "Пользователь не создан, т.к. не определен id.");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("karta@ya.ru");

        User userUpdate = userStorage.update(user);

        assertEquals(user.getEmail(), userStorage.getById(user.getId())
                        .orElseThrow(() -> new NotFoundException("Не найден обновляемый пользователь")).getEmail(),
                "Обновление пользователя не выполнено.");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("erfhihhdk@ya.ru");
        user.setLogin("Genri");
        user.setName("");
        user.setBirthday(stringToInstantConverter.convert("1978-05-22"));

        user = userStorage.add(user);

        long id = user.getId();

        userStorage.delete(user);

        Optional<User> optionalUser = userStorage.getById(id);

        assertTrue(optionalUser.isEmpty(), "Пользователь не удален из БД.");
    }

    @Test
    public void testFriendship() {
        /* тестирование двусторонней связи
        User user = userStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 1."));

        User friend = userStorage.getById(2L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 2."));

        userStorage.linkFriends(user, friend); //1 + 2

        //Проверим, что пользователь 2 в друзьях у пользователя 1 в статусе запроса.
        Map<User, StatusFriendship> friends = userStorage.getFriendsByUser(user);

        assertTrue(friends.containsKey(friend), "Пользователь 2 не добавлен в друзья пользователю 1");

        if (friends.containsKey(friend)) {
            assertEquals(StatusFriendship.NOT_CONFIRM, friends.get(friend), "1<-2 не верная связь.");
        }

        //Проверим, что пользователь 1 в друзьях у пользователя 2 в статусе подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(friend);

        assertTrue(friends.containsKey(user), "Пользователь 1 не добавлен в друзья пользователю 2");

        if (friends.containsKey(friend)) {
            assertEquals(StatusFriendship.CONFIRM, friends.get(friend), "2<-1 е верная связь.");
        }

        //Удалим связь.
        userStorage.deleteLinkFriends(user, friend);

        //Проверим, что пользователь 2 НЕТ в друзьях у пользователя 1.
        friends = userStorage.getFriendsByUser(user);

        assertFalse(friends.containsKey(friend), "Пользователь 2 не удален из друзей пользователя 1");

        //Проверим, что пользователь 1 в друзьях у пользователя 2 в статусе подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(friend);

        assertFalse(friends.containsKey(user), "Пользователь 1 не удален из друзей пользователя 2");
        */

        // тестирование односторонней связи
        User user = userStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 1."));

        User friend = userStorage.getById(2L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 2."));

        userStorage.linkFriends(user, friend); //user <- friend

        //Проверим, что пользователь friend не добавлен в друзья у пользователя user.
        Map<User, StatusFriendship> friends = userStorage.getFriendsByUser(user);

        assertFalse(friends.containsKey(friend), "Пользователь friend добавлен в друзья пользователю user.");

        //Проверим, что пользователь user в друзьях у пользователя friend в статусе НЕ подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(friend);

        assertTrue(friends.containsKey(user), "Пользователь user не добавлен в друзья пользователю friend.");

        if (friends.containsKey(friend)) {
            assertEquals(StatusFriendship.NOT_CONFIRM, friends.get(friend), "friend<-user не верная связь.");
        }

        //Проверим запрос user в друзья пользователю friend.
        userStorage.linkFriends(friend, user); //friend <- user

        //Проверим, что пользователь friend в друзьях у пользователя user в статусе подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(user);

        assertTrue(friends.containsKey(friend), "Пользователь friend не добавлен в друзья пользователю user.");

        if (friends.containsKey(friend)) {
            assertEquals(StatusFriendship.CONFIRM, friends.get(friend), "friend<-user не верная связь.");
        }

        //Проверим, что пользователь user в друзьях у пользователя friend в статусе подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(friend);

        assertTrue(friends.containsKey(user), "Пользователь user не добавлен в друзья пользователю friend.");

        if (friends.containsKey(friend)) {
            assertEquals(StatusFriendship.CONFIRM, friends.get(friend), "friend<-user не верная связь.");
        }

        //Удалим связь.
        userStorage.deleteLinkFriends(user, friend);

        //Проверим, что пользователь 2 НЕТ в друзьях у пользователя 1.
        friends = userStorage.getFriendsByUser(user);

        assertFalse(friends.containsKey(friend), "Пользователь 2 не удален из друзей пользователя 1");

        //Проверим, что пользователь 1 в друзьях у пользователя 2 в статусе подтвержденной дружбы.
        friends = userStorage.getFriendsByUser(friend);

        assertFalse(friends.containsKey(user), "Пользователь 1 не удален из друзей пользователя 2");

    }

    @Test
    public void testCommonFriends() {
        // тестирование двусторонней дружбы
        /*
        User user1 = userStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 1."));

        User user2 = userStorage.getById(2L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 2."));

        User user3 = userStorage.getById(3L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 3."));

        userStorage.linkFriends(user1, user3);

        userStorage.linkFriends(user2, user3);

        Collection<User> userCollection = userStorage.findCommon(user1, user2);

        //Проверим, что друг 1 и это user3.
        assertEquals(1, userCollection.size(), "Друзей больше одного.");

        assertTrue(userCollection.contains(user3), "Общий друг не пользователь 3.");
         */

        // тестирование односторонней дружбы
        User user1 = userStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 1."));

        User user2 = userStorage.getById(2L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 2."));

        User user3 = userStorage.getById(3L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 3."));

        User user4 = userStorage.getById(4L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования дружбы по id = 4."));

        userStorage.linkFriends(user1, user3);

        userStorage.linkFriends(user2, user3);

        userStorage.linkFriends(user2, user4);

        Collection<User> userCollection = userStorage.findCommon(user3, user4);

        //Проверим, что друг 1 и это user2.
        assertEquals(1, userCollection.size(), "Друзей больше одного.");

        assertTrue(userCollection.contains(user2), "Общий друг не пользователь 2.");
    }

    @Test
    public void testFindAllUsers() {
        Collection<User> userCollection = userStorage.findAll();

        //Просто проверим, что пользователи есть. Т.к. создавал перед тестом и в тестах удалял предварительно созданных.
        assertFalse(userCollection.isEmpty(), "Не возвращены данные о пользователях из БД.");
    }

    @Test
    public void testlikes() {
        User user = userStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь для тестирования лайков по id = 1."));

        Film film = filmStorage.getById(1L)
                .orElseThrow(() -> new NotFoundException("Не найден фильм для тестирования лайков по id = 1."));


        filmStorage.addLike(film, user);

        // Проверим, что пользователь поставил лайк фильму.
        Collection<User> userCollection = userStorage.getUsersLikesByFilm(film);

        assertEquals(1, userCollection.size(), "Лайков более одного.");

        assertTrue(userCollection.contains(user), "Не записана в БД информация о лайке фильму.");

        assertEquals(1, filmStorage.getNumberLikes(film), "Количество лайков более 1.");

        // Удалим лайк.
        filmStorage.deleteLike(film, user);

        // Проверим, что пользователь удалил лайк фильму.
        userCollection = userStorage.getUsersLikesByFilm(film);

        assertEquals(0, userCollection.size(), "Лайков более 0.");

        assertFalse(userCollection.contains(user), "Не записана в БД информация о удалении лайка фильму.");

        assertEquals(0, filmStorage.getNumberLikes(film), "Количество лайков более 0.");
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testAddFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Genre genre = new Genre();
        genre.setId(1L);

        Set<Genre> genreSet = new HashSet<>();
        genreSet.add(genre);

        Film film = new Film();
        film.setName("Космос");
        film.setDescription("Приключение в космосе.");
        film.setReleaseDate(stringToInstantConverter.convert("2000-10-25"));
        film.setDuration(integerToDurationConverter.convert(7200));
        film.setRating(mpa);
        film.setGenre(genreSet);

        Film filmAdd = filmStorage.add(film);

        assertNotEquals(0L, filmAdd.getId().longValue(),
                "Фильм не создан, т.к. не определен id.");
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Прогулка!");

        Film filmUpdate = filmStorage.update(film);

        assertEquals(filmUpdate.getName(), filmStorage.getById(film.getId())
                        .orElseThrow(() -> new NotFoundException("Не найден обновляемый фильм")).getName(),
                "Обновление фильма не выполнено.");
    }

    @Test
    public void testDeleteFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Genre genre = new Genre();
        genre.setId(1L);

        Set<Genre> genreSet = new HashSet<>();
        genreSet.add(genre);

        Film film = new Film();
        film.setName("Космос");
        film.setDescription("Приключение в космосе.");
        film.setReleaseDate(stringToInstantConverter.convert("2000-10-25"));
        film.setDuration(integerToDurationConverter.convert(7200));
        film.setRating(mpa);
        film.setGenre(genreSet);

        Film filmAdd = filmStorage.add(film);

        long id = film.getId();

        filmStorage.delete(film);

        Optional<Film> optionalFilm = filmStorage.getById(id);

        assertTrue(optionalFilm.isEmpty(), "Фильм не удален из БД.");
    }

    @Test
    public void testFindAllFilms() {
        Collection<Film> filmCollection = filmStorage.findAll();

        //Просто проверим, что фильмы есть. Т.к. создавал перед тестом и в тестах удалял предварительно созданных.
        assertFalse(filmCollection.isEmpty(), "Не возвращены данные о фильмах из БД.");
    }
}
