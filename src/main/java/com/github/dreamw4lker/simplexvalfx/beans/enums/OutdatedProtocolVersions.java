package com.github.dreamw4lker.simplexvalfx.beans.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Устаревшие версии протоколов: имеют только одну XSD-схему и не имеют Schematron-файлов
 */
public enum OutdatedProtocolVersions {
    LAB_V4,
    CITOL_V1;

    public static List<String> getListValues() {
        return Arrays.stream(values())
                .map(Enum::toString)
                .toList();
    }
}
