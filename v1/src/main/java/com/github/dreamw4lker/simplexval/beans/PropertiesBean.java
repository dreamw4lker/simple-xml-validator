package com.github.dreamw4lker.simplexval.beans;

import com.github.dreamw4lker.simplexval.enums.ValidatorMode;

import java.util.List;

/**
 * Бин с параметрами приложения
 */
public class PropertiesBean {
    private final ValidatorMode validatorMode;
    private final List<String> xmlFilenames;
    private final List<String> xsdFilenames;
    private final List<String> schematronFilenames;
    private final boolean clearXmlNamespaceOnSchematronValidation;

    public PropertiesBean(ValidatorMode validatorMode, List<String> xmlFilenames, List<String> xsdFilenames,
                          List<String> schematronFilenames, boolean clearXmlNamespaceOnSchematronValidation) {
        this.validatorMode = validatorMode;
        this.xmlFilenames = xmlFilenames;
        this.xsdFilenames = xsdFilenames;
        this.schematronFilenames = schematronFilenames;
        this.clearXmlNamespaceOnSchematronValidation = clearXmlNamespaceOnSchematronValidation;
    }

    public ValidatorMode getValidatorMode() {
        return validatorMode;
    }

    public List<String> getXmlFilenames() {
        return xmlFilenames;
    }

    public List<String> getXsdFilenames() {
        return xsdFilenames;
    }

    public List<String> getSchematronFilenames() {
        return schematronFilenames;
    }

    public boolean isClearXmlNamespaceOnSchematronValidation() {
        return clearXmlNamespaceOnSchematronValidation;
    }
}
