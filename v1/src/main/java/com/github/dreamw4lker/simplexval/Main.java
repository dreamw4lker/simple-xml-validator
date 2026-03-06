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
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        //Чтение параметров приложения
        PropertiesBean properties = new PropertiesHelper().initializeProperties();

        log.info("Validation started");
        for (String xmlFilename : properties.getXmlFilenames()) {
            //Чтение XML-файла в строку
            String content = IOUtils.toString(new FileInputStream(xmlFilename), StandardCharsets.UTF_8);

            log.info("XML file: «{}»", xmlFilename);

            //Валидация по XSD
            List<ValidationResult> XSDResults = new ArrayList<>();
            for (String xsdFilename : properties.getXsdFilenames()) {
                XSDResults.add(new XSDValidator().validate(content, properties.getValidatorMode(), xsdFilename));
            }

            //Валидация по Schematron.
            //В некоторых Schematron-файлах не указан namespace по умолчанию.
            //Если установлен флаг, вырезаем его и из прочитанного документа
            if (properties.isClearXmlNamespaceOnSchematronValidation()) {
                content = content.replaceFirst("xmlns=\".*?\"", "xmlns=\"\"");
            }

            List<ValidationResult> schematronResults = new ArrayList<>();
            for (String schematronFilename : properties.getSchematronFilenames()) {
                schematronResults.add(new SchematronValidator().validate(content, properties.getValidatorMode(), schematronFilename));
            }

            log.info("Validation completed for file «{}»", xmlFilename);
            log.info("Results:");
            log.info("  XSD validation:");
            for (int i = 0; i < XSDResults.size(); i++) {
                ValidationResult result = XSDResults.get(i);
                log.info("{} XSD.{}: {}", result.getIcon(), (i + 1), result);
            }

            log.info("  Schematron validation:");
            for (int i = 0; i < schematronResults.size(); i++) {
                ValidationResult result = schematronResults.get(i);
                log.info("{} Schematron.{}: {}", result.getIcon(), (i + 1), result);
            }
        }
    }
}
