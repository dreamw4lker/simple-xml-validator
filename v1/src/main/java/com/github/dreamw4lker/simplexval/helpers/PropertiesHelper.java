package com.github.dreamw4lker.simplexval.helpers;

import com.github.dreamw4lker.simplexval.beans.PropertiesBean;
import com.github.dreamw4lker.simplexval.enums.ValidatorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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

        //Получение XML-файлов для проверки
        List<String> xmlFilenames = new ArrayList<>();
        int index = 1;
        String propPrefix = "validator.input.xml.";
        while (properties.containsKey(propPrefix + index)) {
            String xmlFilename = properties.getProperty(propPrefix + index);
            if (xmlFilename == null || xmlFilename.isEmpty()) {
                log.error("Incorrect «{}» value: XML file must be specified", propPrefix + index);
                throw new IllegalArgumentException();
            }
            xmlFilenames.add(xmlFilename);
            index++;
        }

        //Получение флага, следует ли очищать namespace при валидации через Schematron
        String isClearNamespaceString = properties.getProperty("validator.input.xml.clear-xml-namespace-on-schematron-validation");
        boolean isClearNamespace;
        if (isClearNamespaceString == null || !Set.of("true", "false").contains(isClearNamespaceString.toLowerCase())) {
            log.error("Incorrect «validator.input.xml.clear-xml-namespace-on-schematron-validation» value: must be true or false");
            throw new IllegalArgumentException();
        } else {
            isClearNamespace = Boolean.parseBoolean(isClearNamespaceString);
        }

        //Получение XSD-файлов со схемами (только в режимах XSD и ALL)
        List<String> xsdFilenames = new ArrayList<>();
        if (Set.of(ValidatorMode.XSD, ValidatorMode.ALL).contains(validatorMode)) {
            index = 1;
            propPrefix = "validator.input.xsd.";
            while (properties.containsKey(propPrefix + index)) {
                String xsdFilename = properties.getProperty(propPrefix + index);
                if (xsdFilename == null || xsdFilename.isEmpty()) {
                    log.error("Incorrect «{}» value: XSD (XML Schema definition) file must be specified", propPrefix + index);
                    throw new IllegalArgumentException();
                }
                xsdFilenames.add(xsdFilename);
                index++;
            }
        }

        //Получение Schematron-файлов (только в режимах SCHEMATRON и ALL)
        List<String> schematronFilenames = new ArrayList<>();
        if (Set.of(ValidatorMode.SCHEMATRON, ValidatorMode.ALL).contains(validatorMode)) {
            index = 1;
            propPrefix = "validator.input.schematron.";
            while (properties.containsKey(propPrefix + index)) {
                String schematronFilename = properties.getProperty(propPrefix + index);
                if (schematronFilename == null || schematronFilename.isEmpty()) {
                    log.error("Incorrect «{}» value: Schematron file must be specified", propPrefix + index);
                    throw new IllegalArgumentException();
                }
                schematronFilenames.add(schematronFilename);
                index++;
            }
        }

        return new PropertiesBean(validatorMode, xmlFilenames, xsdFilenames, schematronFilenames, isClearNamespace);
    }
}
