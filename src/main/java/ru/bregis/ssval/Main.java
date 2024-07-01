package ru.bregis.ssval;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.StringStreamSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static SchematronOutputType validateXMLViaPureSchematron(@Nonnull final File schematronFile, String fixedXmlFile) throws Exception {
        final ISchematronResource aResSCH = SchematronResourceSCH.fromFile(schematronFile);
        if (!aResSCH.isValidSchematron())
            throw new IllegalArgumentException("Invalid Schematron!");
        return aResSCH.applySchematronValidationToSVRL(new StringStreamSource(fixedXmlFile));
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            log.error("Please, define three arguments:\n1. XML document\n2. XSD file\n3. Schematron file");
            return;
        }
        //Читаем документ в строку
        String content = IOUtils.toString(new FileInputStream(args[0]), StandardCharsets.UTF_8);
        log.info("[STEP 1]: Validating document «{}» with XSD schema «{}». Please, wait...", args[0], args[1]);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(new File(args[1])));
        Validator validator = schema.newValidator();
        XmlErrorHandler xsdErrorHandler = new XmlErrorHandler();
        validator.setErrorHandler(xsdErrorHandler);
        validator.validate(new StreamSource(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));

        List<SAXParseException> saxExceptions = xsdErrorHandler.getExceptions();
        for (int i = 0; i < saxExceptions.size(); i++) {
            SAXParseException exception = saxExceptions.get(i);
            log.error("-----===== Parse exception #{} =====-----", i + 1);
            log.error("Line: {}, column: {}", exception.getLineNumber(), exception.getColumnNumber());
            log.error("Exception: {}", exception.getMessage());
        }
        log.info("[STEP 1]: XSD validation finished: Document «{}» is {} for XSD «{}»", args[0], saxExceptions.size() > 0 ? "NOT VALID" : "VALID", args[1]);

        //В Schematron минздрава не указан namespace по умолчанию.
        //Вырезаем его из прочитанного документа
        content = content.replaceFirst("<ClinicalDocument xmlns=\"urn:hl7-org:v3\"", "<ClinicalDocument xmlns=\"\"");

        log.info("[STEP 2]: Validating document «{}» with Schematron «{}». Please, wait...", args[0], args[2]);
        SchematronOutputType result = validateXMLViaPureSchematron(new File(args[2]), content);
        List<SVRLFailedAssert> assertList = SVRLHelper.getAllFailedAssertions(result);
        for (int i = 0; i < assertList.size(); i++) {
            SVRLFailedAssert failedAssert = assertList.get(i);
            log.error("-----===== Failed rule #{} =====-----", i + 1);
            log.error("Element: {}", failedAssert.getLocation());
            log.error("Rule: {}", failedAssert.getText());
        }
        log.info("[STEP 2]: Schematron validation finished: Document «{}» is {} for Schematron «{}»", args[0], assertList.size() > 0 ? "NOT VALID" : "VALID", args[2]);
    }
}
