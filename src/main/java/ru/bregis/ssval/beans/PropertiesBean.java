package ru.bregis.ssval.beans;

import ru.bregis.ssval.enums.ValidatorMode;

/**
 * Бин с параметрами приложения
 */
public class PropertiesBean {
    private final ValidatorMode validatorMode;
    private final String xmlFilename;
    private final String xsdFilename;
    private final String schematronFilename;

    public PropertiesBean(ValidatorMode validatorMode, String xmlFilename, String xsdFilename, String schematronFilename) {
        this.validatorMode = validatorMode;
        this.xmlFilename = xmlFilename;
        this.xsdFilename = xsdFilename;
        this.schematronFilename = schematronFilename;
    }

    public ValidatorMode getValidatorMode() {
        return validatorMode;
    }

    public String getXmlFilename() {
        return xmlFilename;
    }

    public String getXsdFilename() {
        return xsdFilename;
    }

    public String getSchematronFilename() {
        return schematronFilename;
    }
}
