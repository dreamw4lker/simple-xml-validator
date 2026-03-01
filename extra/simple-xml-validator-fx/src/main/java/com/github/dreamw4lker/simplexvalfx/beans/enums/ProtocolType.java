package com.github.dreamw4lker.simplexvalfx.beans.enums;

import com.github.dreamw4lker.simplexvalfx.beans.ProtocolTypeVersionBean;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ProtocolType {
    LAB("LAB", "Протокол лабораторного исследования", "1.2.643.5.1.13.13.15.18", List.of(4, 5)),
    CITOL("CITOL", "Протокол цитологического исследования", "1.2.643.5.1.13.13.15.20", List.of(1, 2, 3)),
    MBIO("MBIO", "Заключение по результатам МБИО-исследования", "1.2.643.5.1.13.13.15.120", List.of(1)),
    CONSULT("CONSULT", "Протокол консультации", "1.2.643.5.1.13.13.15.13", List.of(5)),
    CTA("CTA", "Справка о результатах ХТИ", "1.2.643.5.1.13.13.15.19", List.of(2)),
    PATAN("PATAN", "Протокол прижизн. патологоанатом. иссл. ", "1.2.643.5.1.13.13.15.21", List.of(3));

    private final String code;
    private final String name;
    private final String oid;
    private final List<Integer> versions;

    public static List<ProtocolTypeVersionBean> getProtocolTypeVersionBeans() {
        List<ProtocolTypeVersionBean> result = new ArrayList<>();
        for (ProtocolType protocolType : ProtocolType.values()) {
            for (Integer protocolTypeVersion : protocolType.getVersions()) {
                String code = protocolType.getCode() + "_V" + protocolTypeVersion;

                List<Path> xsdFilenames = new ArrayList<>();
                xsdFilenames.add(Paths.get(".", "protocols", code, "xsd", "XSD_CDA", "CDA.xsd"));
                if (!List.of("LAB_V4", "CITOL_V1").contains(code)) { //todo extract to external
                    xsdFilenames.add(Paths.get(".", "protocols", code, "xsd", "XSD_CDA_" + code, "xsd", "CDA.xsd"));
                }

                List<Path> schematronFilenames = new ArrayList<>();
                if (!List.of("LAB_V4", "CITOL_V1").contains(code)) { //todo extract to external
                    schematronFilenames.add(Paths.get(".", "protocols", code, "schematron", "schematron.sch"));
                }
                result.add(new ProtocolTypeVersionBean(
                        code,
                        protocolType.getName() + " (v" + protocolTypeVersion + ")",
                        xsdFilenames,
                        schematronFilenames
                ));
            }
        }
        return result;
    }
}
