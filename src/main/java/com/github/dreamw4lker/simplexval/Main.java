package com.github.dreamw4lker.simplexval;

import com.github.dreamw4lker.simplexval.beans.PropertiesBean;
import com.github.dreamw4lker.simplexval.enums.ValidationResult;
import com.github.dreamw4lker.simplexval.helpers.PropertiesHelper;
import com.github.dreamw4lker.simplexval.validator.schematron.SchematronValidator;
import com.github.dreamw4lker.simplexval.validator.xsd.XSDValidator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(XSDValidator.class);

    public static void main(String[] args) throws Exception {
        //Чтение параметров приложения
        PropertiesBean properties = new PropertiesHelper().initializeProperties();

        //Чтение XML-файла в строку
        String content = IOUtils.toString(new FileInputStream(properties.getXmlFilename()), StandardCharsets.UTF_8);

        log.info("Validation started");
        log.info("XML file: «{}»", properties.getXmlFilename());

        //Валидация по XSD
        ValidationResult XSDResult = new XSDValidator().validate(content, properties);

        //Валидация по Schematron.
        //В некоторых Schematron-файлах не указан namespace по умолчанию.
        //Если установлен флаг, вырезаем его и из прочитанного документа
        if (properties.isClearXmlNamespaceOnSchematronValidation()) {
            content = content.replaceFirst("xmlns=\".*?\"", "xmlns=\"\"");
        }
        ValidationResult schematronResult = new SchematronValidator().validate(content, properties);

        log.info("Validation completed");
        log.info("Results:");
        log.info("  XSD validation: {}", XSDResult);
        log.info("  Schematron validation: {}", schematronResult);
    }
}
