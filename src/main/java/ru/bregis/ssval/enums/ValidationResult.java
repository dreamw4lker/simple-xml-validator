package ru.bregis.ssval.enums;

/**
 * Перечисление результатов валидации
 */
public enum ValidationResult {
    VALID("VALID"),
    NOT_VALID("NOT VALID");

    final String text;

    ValidationResult(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
