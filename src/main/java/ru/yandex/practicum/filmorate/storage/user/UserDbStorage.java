package ru.yandex.practicum.filmorate.storage.user;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.extractors.FriendsResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.util.Reflection;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM user_;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM user_ WHERE id = ?;";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM user_ WHERE LOWER(email) = LOWER(?)";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM user_ WHERE LOWER(login) = LOWER(?)";
    private static final String INSERT_QUERY = "INSERT INTO user_ (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?);";
    private static final String UPDATE_QUERY = "UPDATE user_ SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE id = ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM user_ WHERE id = ?;";
    private static final String FIND_USERS_LIKES_BY_FILM_QUERY = "SELECT * FROM User_" +
            " WHERE id IN (SELECT user_id FROM likes WHERE film_id = :film_id);";
    private static final String FIND_FRIENDS_BY_USER_QUERY =
            "SELECT u.id AS id, u.email AS email, u.login AS login, u.name AS name, u.birthday AS birthday," +
                    " sf.name AS nameSF" +
                    " FROM user_ AS u" +
                    " INNER JOIN friends AS f ON f.friend_id = u.id" +
                    "   AND f.user_id = :user_id" +
                    " INNER JOIN StatusFriendship AS sf ON sf.id = f.status_id;";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id, status_id)" +
            " VALUES (" +
            ":user_id, " +
            ":friend_id, " +
            "(SELECT sf.id FROM STATUSFRIENDSHIP sf WHERE sf.name = :status_name LIMIT 1));";
    private static final String UPDATE_FRIEND_QUERY = "UPDATE friends" +
            " SET status_id = (SELECT sf.id FROM STATUSFRIENDSHIP sf WHERE sf.name = :status_name LIMIT 1)" +
            " WHERE user_id = :user_id AND friend_id = :friend_id;";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends" +
            " WHERE user_id IN (:user_ids) AND friend_id IN (:friend_ids);";
    private static final String DELETE_USER_IN_FRIENDS_QUERY = "DELETE FROM friends" +
            " WHERE user_id = :user_id OR friend_id = :user_id;";
    private static final String DELETE_USER_IN_LIKES_QUERY = "DELETE FROM likes" +
            " WHERE user_id = :user_id;";

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserDbStorage.class);

    @Autowired
    FriendsResultSetExtractor friendsResultSetExtractor;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User add(User user) {
        //Если пользователь с указанным адресом электронной почты уже был добавлен ранее,
        // то генерируется исключение DuplicatedDataException с описанием: "Этот имейл уже используется".
        if (getByEmail(user.getEmail()).isPresent())
            throw new DuplicatedDataException("Нельзя создать пользователя по причине: " +
                    "этот имейл уже используется.", log);

        //Если пользователь с указанным логином уже был добавлен ранее,
        // то генерируется исключение DuplicatedDataException с описанием: "Этот логин уже используется".
        if (getByLogin(user.getLogin()).isPresent())
            throw new DuplicatedDataException("Нельзя создать пользователя по причине: " +
                    "этот логин уже используется.", log);

        // формируем дополнительные данные
        if (StringUtils.isBlank(user.getName()))
            user.setName(user.getLogin());  //имя для отображения может быть пустым — в таком случае будет использован логин

        // сохраняем нового пользователя в БД приложения
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(user.getBirthday())
        );
        user.setId(id);

        log.info("Добавлен новый пользователь {}.", user);

        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getById(newUser.getId()).
                orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден.", log));

        if (!StringUtils.isBlank(newUser.getEmail())) {
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                //Если при обновлении данных пользователя, указан новый адрес электронной почты и
                // в приложении уже есть пользователь с таким адресом,
                // то должно генерироваться исключение DuplicatedDataException с описанием: "Этот имейл уже используется".
                if (getByEmail(newUser.getEmail()).isPresent())
                    throw new DuplicatedDataException("Нельзя обновить пользователя по причине: " +
                            "нельзя использовать имейл, который уже используется.", log);
            }
        }

        if (!StringUtils.isBlank(newUser.getLogin())) {
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                //Если при обновлении данных пользователя, указан новый логин и
                // в приложении уже есть пользователь с таким логином,
                // то должно генерироваться исключение DuplicatedDataException с описанием: "Этот логин уже используется".
                if (getByLogin(newUser.getLogin()).isPresent())
                    throw new DuplicatedDataException("Нельзя обновить пользователя по причине: " +
                            "нельзя использовать логин, который уже используется.", log);
            }
        }

        // обновляем содержимое объекта oldUser
        BeanUtils.copyProperties(newUser, oldUser, Reflection.getIgnoreProperties(newUser));

        // обновляем данные пользователя в БД.
        update(
                UPDATE_QUERY,
                oldUser.getEmail(),
                oldUser.getLogin(),
                oldUser.getName(),
                Timestamp.from(oldUser.getBirthday()),
                oldUser.getId());

        log.info("Обновлены данные пользователя {}.", oldUser);

        return oldUser;
    }

    @Override
    @Transactional
    public User delete(User user) {
        SqlParameterSource parameters = new MapSqlParameterSource("user_id", user.getId());

        // удаляем данные о дружеских связях, в которых участвовал пользователь, из БД
        deleteParameterSource(DELETE_USER_IN_FRIENDS_QUERY, parameters);

        // удаляем записи о лайках, которые пользователь ставил фильмам, из БД
        deleteParameterSource(DELETE_USER_IN_LIKES_QUERY, parameters);

        // удаляем пользователя из БД приложения
        delete(DELETE_BY_ID_QUERY, user.getId());

        log.info("Удален пользователь {}.", user);

        return user;
    }

    @Override
    public Optional<User> getById(Long userId) {
        log.info("Запрошена информация по пользователю с id {}.", userId);

        Optional<User> optionalUser = findOne(FIND_BY_ID_QUERY, userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Map<User, StatusFriendship> friendshipMap = getFriendsByUser(user);

            user.setFriends(friendshipMap);
        }

        return optionalUser;
    }

    public Optional<User> getByEmail(String email) {
        log.info("Запрошена информация по пользователю с email {}.", email);

        Optional<User> optionalUser = findOne(FIND_BY_EMAIL_QUERY, email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Map<User, StatusFriendship> friendshipMap = getFriendsByUser(user);

            user.setFriends(friendshipMap);
        }

        return optionalUser;
    }

    public Optional<User> getByLogin(String login) {
        log.info("Запрошена информация по пользователю с логином {}.", login);

        Optional<User> optionalUser = findOne(FIND_BY_LOGIN_QUERY, login);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Map<User, StatusFriendship> friendshipMap = getFriendsByUser(user);

            user.setFriends(friendshipMap);
        }

        return optionalUser;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получен список пользователей.");

        return findMany(FIND_ALL_QUERY).stream()
                .peek(user -> user.setFriends(getFriendsByUser(user)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getUsersLikesByFilm(Film film) {
        log.info("Получен список пользователей поставивших лайк фильму с id {}.", film.getId());

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", film.getId());

        return findManyParameterSource(FIND_USERS_LIKES_BY_FILM_QUERY, parameters).stream()
                .peek(user -> user.setFriends(getFriendsByUser(user)))
                .collect(Collectors.toList());
    }

    /**
     * Обработка запроса в друзья.
     *
     * @param user   - пользователь, кому сделан запрос
     * @param friend - пользователь, который сделал запрос
     */
    @Override
    @Transactional
    public void linkFriends(User user, User friend) {
        //Добавляем пользователей в друзья друг другу.
        // Проверка, что являются друзьями не нужна, так как использую Map.

        //Пользователю user добавляем друга friend.
        Map<User, StatusFriendship> friends = getFriendsByUser(user);
        if (!friends.containsKey(friend)) {
            // если ранее не было дружеской связи (в любом статусе)
            friends.put(friend, StatusFriendship.NOT_CONFIRM);  //не подтвердил свою дружбу, только запрос

            insertFriend(user, friend, StatusFriendship.NOT_CONFIRM);
        }
        user.setFriends(friends);

        //Пользователю friend добавляем друга user.
        friends = getFriendsByUser(friend);
        if (!friends.containsKey(user)) {
            friends.put(user, StatusFriendship.CONFIRM);    //направив запрос, он готов дружить

            insertFriend(friend, user, StatusFriendship.CONFIRM);
        } else if ((friends.get(user)) != StatusFriendship.CONFIRM) {
            friends.put(user, StatusFriendship.CONFIRM);    //направив запрос, он готов дружить

            updateFriend(friend, user, StatusFriendship.CONFIRM);
        }
        friend.setFriends(friends);

        log.info("Пользователем с id {} сделан запрос в друзья к пользователю с id {}.", friend.getId(), user.getId());
    }

    /**
     * Удаление дружеской связи.
     *
     * @param user   - пользователь, у которого нужно удалить друга friend
     * @param friend - пользователь, у которого нужно удалить друга user
     */
    @Override
    public void deleteLinkFriends(User user, User friend) {
        //Удаляем пользователей из друзей друг у друга:
        // пользователю user удаляю друга friend
        // пользователю friend удаляю друга user
        List<Long> longList = new ArrayList<>();
        longList.add(user.getId());
        longList.add(friend.getId());

        SqlParameterSource parameters = new MapSqlParameterSource("user_ids", longList)
                .addValue("friend_ids", longList);

        deleteParameterSource(DELETE_FRIEND_QUERY, parameters);

        log.info("Пользователи с id {} и {} исключены из друзей друг у друга.", user.getId(), friend.getId());
    }

    @Override
    public Collection<User> findCommon(User user1, User user2) {
        log.info("Запрошен список общих друзей пользователей с id {} и {}.", user1.getId(), user2.getId());
        //todo определиться возвращать только подтвержденных друзей или запросы тоже учитывать (сейчас с ними)
        return Stream
                // обращаться к БД для получения данных о друзьях пользователя не нужно,
                // т.к. это сделано при получении данных о пользователе в методе getById.
                .concat(user1.getFriends().keySet().stream(), user2.getFriends().keySet().stream())
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()))
                .entrySet() //результат первого потока, после группировки: друг - количество повторений
                .stream()
                .filter(m -> m.getValue() > 1)  //общий друг
                .map(Map.Entry::getKey) //получаю пользователя
                .toList();
    }

    @Override
    public Map<User, StatusFriendship> getFriendsByUser(User user) {

        SqlParameterSource parameters = new MapSqlParameterSource("user_id", user.getId());

        return findMap(FIND_FRIENDS_BY_USER_QUERY, parameters, friendsResultSetExtractor);
    }

    private void insertFriend(User user, User friend, StatusFriendship statusFriendship) {
        SqlParameterSource parameters = new MapSqlParameterSource("user_id", user.getId())
                .addValue("friend_id", friend.getId())
                .addValue("status_name", statusFriendship.getValue());

        insertParameterSource(INSERT_FRIEND_QUERY, parameters);
    }

    private void updateFriend(User user, User friend, StatusFriendship statusFriendship) {
        SqlParameterSource parameters = new MapSqlParameterSource("user_id", user.getId())
                .addValue("friend_id", friend.getId())
                .addValue("status_name", statusFriendship.getValue());

        updateParameterSource(UPDATE_FRIEND_QUERY, parameters);
    }
}
