package com.github.dreamw4lker.simplexval.beans;

import com.github.dreamw4lker.simplexval.enums.ValidatorMode;

/**
 * Бин с параметрами приложения
 */
public class PropertiesBean {
    private final ValidatorMode validatorMode;
    private final String xmlFilename;
    private final String xsdFilename;
    private final String schematronFilename;
    private final boolean clearXmlNamespaceOnSchematronValidation;

    public PropertiesBean(ValidatorMode validatorMode, String xmlFilename, String xsdFilename,
                          String schematronFilename, boolean clearXmlNamespaceOnSchematronValidation) {
        this.validatorMode = validatorMode;
        this.xmlFilename = xmlFilename;
        this.xsdFilename = xsdFilename;
        this.schematronFilename = schematronFilename;
        this.clearXmlNamespaceOnSchematronValidation = clearXmlNamespaceOnSchematronValidation;
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

    public boolean isClearXmlNamespaceOnSchematronValidation() {
        return clearXmlNamespaceOnSchematronValidation;
    }
}
