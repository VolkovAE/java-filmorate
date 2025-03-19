package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_TIME_FORMATTER_YYYY_DD_MM;

//@Slf4j
public class StringToInstantConverter extends StdConverter<String, Instant> {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StringToInstantConverter.class);

    @Override
    public Instant convert(String value) {
        LocalDate localDate;
        try {
            //localDate = LocalDate.parse(value, DATE_TIME_FORMATTER_DD_MM_YYYY);     //использую свой формат даты
            localDate = LocalDate.parse(value, DATE_TIME_FORMATTER_YYYY_DD_MM); //сделал, чтобы проходил тест практикума
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage(), log);
        }

        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
