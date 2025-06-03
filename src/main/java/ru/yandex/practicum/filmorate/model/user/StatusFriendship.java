package ru.yandex.practicum.filmorate.model.user;

import java.util.Arrays;

public enum StatusFriendship {
    // статусов может быть больше, например блокировка на добавление и пр.
    CONFIRM("Confirm"),
    NOT_CONFIRM("Not confirm");

    private final String value;

    private StatusFriendship(String value) {
        this.value = value;
    }

    public static StatusFriendship fromString(final String s) throws IllegalArgumentException {
        return Arrays.stream(StatusFriendship.values())
                .filter(v -> v.value.equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Не определен статус дружбы : %s", s)));
    }

    public boolean isConfirm() {
        return this == StatusFriendship.CONFIRM;
    }

    public boolean isCNotConfirm() {
        return this == StatusFriendship.NOT_CONFIRM;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (this) {
            case NOT_CONFIRM -> {
                return "Неподтверждённая, когда один пользователь отправил запрос на добавление другого " +
                        "пользователя в друзья";
            }
            case CONFIRM -> {
                return "Подтверждённая, когда второй пользователь согласился на добавление.";
            }
            default -> {
                return super.toString();
            }
        }
    }
}
