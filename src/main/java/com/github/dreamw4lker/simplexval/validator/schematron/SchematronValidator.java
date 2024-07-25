package com.github.dreamw4lker.simplexval.validator.schematron;

import com.github.dreamw4lker.simplexval.enums.ValidationResult;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.StringStreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Валидация XML-файла по Schematron-файлу
 */
public class SchematronValidator {
    private static final Logger log = LoggerFactory.getLogger(SchematronValidator.class);

    public static SchematronOutputType validateXMLViaPureSchematron(File schematronFile, String fixedXmlFile) throws Exception {
        final ISchematronResource aResSCH = SchematronResourceSCH.fromFile(schematronFile);
        if (!aResSCH.isValidSchematron()) {
            throw new IllegalArgumentException("Schematron file is corrupted");
        }
        return aResSCH.applySchematronValidationToSVRL(new StringStreamSource(fixedXmlFile));
    }

    public ValidationResult validate(String xmlContent, String xmlFilename, String schematronFilename) throws Exception {
        log.info("Validating document «{}» with Schematron «{}». Please, wait...", xmlFilename, schematronFilename);

        SchematronOutputType result = validateXMLViaPureSchematron(new File(schematronFilename), xmlContent);
        List<SVRLFailedAssert> assertList = SVRLHelper.getAllFailedAssertions(result);
        for (int i = 0; i < assertList.size(); i++) {
            SVRLFailedAssert failedAssert = assertList.get(i);
            log.error("-----===== Failed rule #{} =====-----", i + 1);
            log.error("Element: {}", failedAssert.getLocation());
            log.error("Rule: {}", failedAssert.getText());
        }

        ValidationResult validationResult = assertList.size() > 0 ? ValidationResult.NOT_VALID : ValidationResult.VALID;
        log.info("Schematron validation finished: Document «{}» is {} for Schematron «{}»", xmlFilename, validationResult, schematronFilename);
        return validationResult;
    }
}
