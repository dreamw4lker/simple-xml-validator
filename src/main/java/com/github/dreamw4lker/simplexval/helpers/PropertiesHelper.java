package com.github.dreamw4lker.simplexval.helpers;

import com.github.dreamw4lker.simplexval.beans.PropertiesBean;
import com.github.dreamw4lker.simplexval.enums.ValidatorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.Properties;
import java.util.Set;

public class PropertiesHelper {
    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    /**
     * Инициализация бина с properties
     */
    public PropertiesBean initializeProperties() throws Exception {
        //Чтение VM options application.properties
        String propertiesPath = System.getProperty("application.properties");
        if (propertiesPath == null || propertiesPath.isEmpty()) {
            //Если параметр не задан, будем искать application.properties, лежащий рядом с запускаемым jar-файлом
            propertiesPath = "application.properties";
        }

        FileReader reader = new FileReader(propertiesPath);
        Properties properties = new Properties();
        properties.load(reader);

        //Получение режима работы валидатора
        ValidatorMode validatorMode;
        try {
            validatorMode = ValidatorMode.valueOf(properties.getProperty("validator.mode"));
        } catch (IllegalArgumentException ex) {
            log.error("Incorrect «validator.mode» value: «{}». Must be one of {}", properties.getProperty("validator.mode"), ValidatorMode.values());
            throw ex;
        }

        //Получение XML-файла для проверки
        String xmlFilename = properties.getProperty("validator.input.xml");
        if (xmlFilename == null || xmlFilename.isEmpty()) {
            log.error("Incorrect «validator.input.xml» value: XML file must be specified");
            throw new IllegalArgumentException();
        }

        //Получение XSD-файла со схемой (только в режимах XSD и ALL)
        String xsdFilename = null;
        if (Set.of(ValidatorMode.XSD, ValidatorMode.ALL).contains(validatorMode)) {
            xsdFilename = properties.getProperty("validator.input.xsd");
            if (xsdFilename == null || xsdFilename.isEmpty()) {
                log.error("Incorrect «validator.input.xsd» value: XSD (XML Schema definition) file must be specified");
                throw new IllegalArgumentException();
            }
        }

        //Получение Schematron-файла (только в режимах SCHEMATRON и ALL)
        String schematronFilename = null;
        if (Set.of(ValidatorMode.SCHEMATRON, ValidatorMode.ALL).contains(validatorMode)) {
            schematronFilename = properties.getProperty("validator.input.schematron");
            if (schematronFilename == null || schematronFilename.isEmpty()) {
                log.error("Incorrect «validator.input.schematron» value: Schematron file must be specified");
                throw new IllegalArgumentException("Incorrect «validator.input.schematron» value: Schematron file must be specified");
            }
        }

        return new PropertiesBean(validatorMode, xmlFilename, xsdFilename, schematronFilename);
    }
}
