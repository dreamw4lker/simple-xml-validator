package com.github.dreamw4lker.simplexvalfx.utils.validator.xsd;

import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidationResult;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidatorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

/**
 * Валидация XML-файла по XSD-схеме
 */
public class XSDValidator {
    private static final Logger log = LoggerFactory.getLogger(XSDValidator.class);

    public ValidationResult validate(String xmlContent, ValidatorMode validatorMode, String xsdFilename) throws SAXException, IOException {
        if (ValidatorMode.SCHEMATRON.equals(validatorMode)) {
            return ValidationResult.SKIPPED;
        }

        log.info("XSD validation started");
        log.info("XSD file: «{}»", xsdFilename);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(new File(xsdFilename)));
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

        ValidationResult validationResult = saxExceptions.size() > 0 ? ValidationResult.NOT_VALID : ValidationResult.VALID;

        log.info("XSD validation completed");
        return validationResult;
    }
}
