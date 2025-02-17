package com.github.dreamw4lker.simplexval.enums;

/**
 * Перечисление результатов валидации
 */
public enum ValidationResult {
    VALID("VALID", "✓"),
    NOT_VALID("NOT VALID", "⨯"),
    SKIPPED("SKIPPED", "~");

    final String text;
    final String icon;

    ValidationResult(String text, String icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return text;
    }
}
