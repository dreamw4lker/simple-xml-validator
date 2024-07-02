package ru.bregis.ssval.validator.xsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ru.bregis.ssval.enums.ValidationResult;

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

    public ValidationResult validate(String xmlContent, String xmlFilename, String xsdFilename) throws SAXException, IOException {
        log.info("Validating document «{}» with XSD schema «{}». Please, wait...", xmlFilename, xsdFilename);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(new File(xsdFilename)));
        Validator validator = schema.newValidator();
        XSDXmlErrorHandler xsdErrorHandler = new XSDXmlErrorHandler();
        validator.setErrorHandler(xsdErrorHandler);
        validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))));

        List<SAXParseException> saxExceptions = xsdErrorHandler.getExceptions();
        for (int i = 0; i < saxExceptions.size(); i++) {
            SAXParseException exception = saxExceptions.get(i);
            log.error("-----===== Parse exception #{} =====-----", i + 1);
            log.error("Line: {}, column: {}", exception.getLineNumber(), exception.getColumnNumber());
            log.error("Exception: {}", exception.getMessage());
        }

        ValidationResult validationResult = saxExceptions.size() > 0 ? ValidationResult.NOT_VALID : ValidationResult.VALID;
        log.info("XSD validation finished: Document «{}» is {} for XSD «{}»", xmlFilename, validationResult, xsdFilename);
        return validationResult;
    }
}
