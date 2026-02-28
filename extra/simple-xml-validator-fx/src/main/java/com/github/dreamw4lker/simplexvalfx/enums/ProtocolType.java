package com.github.dreamw4lker.simplexvalfx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum ProtocolType {
    LAB("LAB", "1.2.643.5.1.13.13.15.18", List.of(4, 5)),
    CITOL("CITOL", "1.2.643.5.1.13.13.15.20", List.of(1, 2, 3)),
    MBIO("MBIO", "1.2.643.5.1.13.13.15.120", List.of(1)),
    CONSULT("CONSULT", "1.2.643.5.1.13.13.15.13", List.of(5)),
    CTA("CTA", "1.2.643.5.1.13.13.15.19", List.of(2)),
    PATAN("PATAN", "1.2.643.5.1.13.13.15.21", List.of(3));

    private final String code;
    private final String oid;
    private final List<Integer> versions;
}
