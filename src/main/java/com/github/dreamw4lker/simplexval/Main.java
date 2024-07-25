package com.github.dreamw4lker.simplexval;

import com.github.dreamw4lker.simplexval.beans.PropertiesBean;
import com.github.dreamw4lker.simplexval.enums.ValidatorMode;
import com.github.dreamw4lker.simplexval.helpers.PropertiesHelper;
import com.github.dreamw4lker.simplexval.validator.schematron.SchematronValidator;
import com.github.dreamw4lker.simplexval.validator.xsd.XSDValidator;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        //Чтение параметров приложения
        PropertiesBean properties = new PropertiesHelper().initializeProperties();

        //Чтение XML-файла в строку
        String content = IOUtils.toString(new FileInputStream(properties.getXmlFilename()), StandardCharsets.UTF_8);

        //Валидация по XSD
        if (Set.of(ValidatorMode.XSD, ValidatorMode.ALL).contains(properties.getValidatorMode())) {
            new XSDValidator().validate(content, properties.getXmlFilename(), properties.getXsdFilename());
        }

        //Валидация по Schematron
        if (Set.of(ValidatorMode.SCHEMATRON, ValidatorMode.ALL).contains(properties.getValidatorMode())) {
            //В Schematron минздрава не указан namespace по умолчанию.
            //Вырезаем его из прочитанного документа
            content = content.replaceFirst("<ClinicalDocument xmlns=\"urn:hl7-org:v3\"", "<ClinicalDocument xmlns=\"\"");
            new SchematronValidator().validate(content, properties.getXmlFilename(), properties.getSchematronFilename());
        }
    }
}
