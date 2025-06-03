package ru.yandex.practicum.filmorate.storage.extractors;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FriendsResultSetExtractor implements ResultSetExtractor<Map<User, StatusFriendship>> {
    private final UserRowMapper userRowMapper;

    @Override
    public Map<User, StatusFriendship> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<User, StatusFriendship> friends = new HashMap<>();

        while (rs.next()) {
            User user = userRowMapper.mapRow(rs, 0);    //номер строки нам здесь не важен

            StatusFriendship statusFriendship = StatusFriendship.fromString(rs.getString("nameSF"));

            friends.put(user, statusFriendship);
        }
        return friends;
    }
}
