package com.github.dreamw4lker.simplexvalfx.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.eclipse.jgit.lib.ProgressMonitor;

public class TextAreaProgressMonitor implements ProgressMonitor {
    private final TextArea textArea;
    private int totalWork;
    private int completed;

    public TextAreaProgressMonitor(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void start(int totalTasks) {
        Platform.runLater(() -> {
            textArea.appendText(String.format("Выполняется задач: %d\n", totalTasks));
        });
    }

    @Override
    public void beginTask(String title, int totalWork) {
        this.totalWork = totalWork;
        this.completed = 0;

        Platform.runLater(() -> {
            textArea.appendText(String.format("\nЗадача: %s\n", title));
            if (totalWork > 0) {
                textArea.appendText(String.format("Всего объектов: %d\n", totalWork));
            }
        });
    }

    @Override
    public void update(int completed) {
        this.completed += completed;
        int percentage = totalWork > 0 ? (this.completed * 100 / totalWork) : 0;

        Platform.runLater(() -> {
            // Обновляем последнюю строку с прогрессом
            String progress = String.format("Прогресс: %d/%d (%d%%) %s\n",
                    this.completed, totalWork, percentage,
                    getProgressBar(percentage));
            textArea.appendText(progress);
        });
    }

    private String getProgressBar(int percentage) {
        int barLength = 50;
        int filled = percentage * barLength / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "=" : " ");
        }
        bar.append("]");
        return bar.toString();
    }

    @Override
    public void endTask() {
        Platform.runLater(() -> {
            textArea.appendText("Задача выполнена\n");
        });
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void showDuration(boolean b) {
    }
}