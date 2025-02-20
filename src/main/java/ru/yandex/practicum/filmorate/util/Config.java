package ru.yandex.practicum.filmorate.util;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config implements EnvironmentAware {
    private Environment environment;

    @Getter
    private static Level levelLog;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;

        init();
    }

    private void init() {
        String value = environment.getProperty("app.LevelLog");

        if (value == null) Config.levelLog = Level.TRACE;
        else if (value.equals("TRACE")) Config.levelLog = Level.TRACE;
        else if (value.equals("DEBUG")) Config.levelLog = Level.DEBUG;
        else if (value.equals("INFO")) Config.levelLog = Level.INFO;
        else if (value.equals("WARN")) Config.levelLog = Level.WARN;
        else if (value.equals("ERROR")) Config.levelLog = Level.ERROR;
        else Config.levelLog = Level.INFO;
    }
}
