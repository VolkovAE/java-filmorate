package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final StringToInstantConverter stringToInstantConverter;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));

//        Timestamp birthday = resultSet.getTimestamp("birthday");
//        user.setBirthday(birthday.toInstant());
        user.setBirthday(stringToInstantConverter.convert(resultSet.getString("birthday")));

        return user;
    }
}
