package com.github.dreamw4lker.simplexvalfx.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import com.github.dreamw4lker.simplexvalfx.configuration.TextAreaLogbackAppender;
import javafx.scene.control.TextArea;
import org.slf4j.LoggerFactory;

public class LoggingUtils {
    private static final String LOGGING_PATTERN = "%d{HH:mm:ss} [%-5level] - %msg%n";

    public static void setupLogging(TextArea textArea, Class clazz) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Настраиваем формат (Layout)
        PatternLayout layout = new PatternLayout();
        layout.setPattern(LOGGING_PATTERN);
        layout.setContext(lc);
        layout.start();

        // Создаем и запускаем наш аппендер
        TextAreaLogbackAppender appender = new TextAreaLogbackAppender(textArea, layout);
        appender.setContext(lc);
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        logger.addAppender(appender);
    }
}
