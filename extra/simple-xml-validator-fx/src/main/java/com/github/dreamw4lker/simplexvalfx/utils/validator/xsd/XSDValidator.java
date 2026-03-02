package com.github.dreamw4lker.simplexvalfx.utils.validator.xsd;

import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidationResult;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidatorMode;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Валидация XML-файла по XSD-схеме
 */
@Slf4j
public class XSDValidator {
    public ValidationResult validate(String xmlContent, ValidatorMode validatorMode, Path xsdPath) {
        if (ValidatorMode.SCHEMATRON.equals(validatorMode)) {
            return ValidationResult.SKIPPED;
        }

        try {
            log.info("XSD validation started");
            log.info("XSD file: «{}»", xsdPath);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(xsdPath.toFile()));
            Validator validator = schema.newValidator();
            XSDXmlErrorHandler xsdErrorHandler = new XSDXmlErrorHandler();
            validator.setErrorHandler(xsdErrorHandler);
            validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))));

            List<SAXParseException> saxExceptions = xsdErrorHandler.getExceptions();
            for (int i = 0; i < saxExceptions.size(); i++) {
                SAXParseException exception = saxExceptions.get(i);
                log.error("→ XSD exception #{}", i + 1);
                log.error("  Line: {}, column: {}", exception.getLineNumber(), exception.getColumnNumber());
                log.error("  Message: {}", exception.getMessage());
            }

            ValidationResult validationResult = saxExceptions.isEmpty() ? ValidationResult.VALID : ValidationResult.NOT_VALID;

            log.info("XSD validation completed");
            return validationResult;
        } catch (SAXException | IOException e) {
            log.error("XSD validation exception", e);
            return ValidationResult.NOT_VALID;
        }
    }
}
