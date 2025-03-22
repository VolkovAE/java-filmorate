package ru.yandex.practicum.filmorate.model.film;

import java.util.Arrays;

public enum Rating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String value;

    private Rating(String value) {
        this.value = value;
    }

    public static Rating fromString(final String s) throws IllegalArgumentException {
        return Arrays.stream(Rating.values())
                .filter(v -> v.value.equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Ассоциации кинокомпаний не " +
                        "поддерживает рейтинг : %s", s)));
    }

    public boolean isG() {
        return this == Rating.G;
    }

    public boolean isPG() {
        return this == Rating.PG;
    }

    public boolean isPG13() {
        return this == Rating.PG13;
    }

    public boolean isR() {
        return this == Rating.R;
    }

    public boolean isNC17() {
        return this == Rating.NC17;
    }

    @Override
    public String toString() {
        switch (this) {
            case G -> {
                return "У фильма нет возрастных ограничений";
            }
            case PG -> {
                return "Детям рекомендуется смотреть фильм с родителями";
            }
            case PG13 -> {
                return "Детям до 13 лет просмотр не желателен";
            }
            case R -> {
                return "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого";
            }
            case NC17 -> {
                return "Лицам до 18 лет просмотр запрещён";
            }
            default -> {
                return super.toString();
            }
        }
    }
}
