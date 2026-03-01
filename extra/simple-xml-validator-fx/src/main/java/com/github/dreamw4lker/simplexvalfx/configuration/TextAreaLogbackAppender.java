package com.github.dreamw4lker.simplexvalfx.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaLogbackAppender extends AppenderBase<ILoggingEvent> {
    private final TextArea textArea;
    private final Layout<ILoggingEvent> layout;

    public TextAreaLogbackAppender(TextArea textArea, Layout<ILoggingEvent> layout) {
        this.textArea = textArea;
        this.layout = layout;
    }

    @Override
    protected void append(ILoggingEvent event) {
        String formattedMessage = layout.doLayout(event);
        // Обновляем UI в специальном потоке JavaFX
        Platform.runLater(() -> {
            textArea.appendText(formattedMessage);
        });
    }
}
