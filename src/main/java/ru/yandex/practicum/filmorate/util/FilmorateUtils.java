package ru.yandex.practicum.filmorate.util;

import java.util.Map;

public final class FilmorateUtils {
    private FilmorateUtils() {

    }

    public static <T> long getNextId(Map<Long, T> map) {
        long currentMaxId = map.keySet().stream() //открыл поток Stream<Long>
                .mapToLong(id -> id)    //преобразовал поток Stream<Long> (поток объектов Long)
                // в поток значений примитивного типа long StreamLong,
                // чтобы воспользоваться max
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
