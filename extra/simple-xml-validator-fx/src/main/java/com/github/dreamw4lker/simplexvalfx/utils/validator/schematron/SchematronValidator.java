package com.github.dreamw4lker.simplexvalfx.utils.validator.schematron;

import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidationResult;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidatorMode;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.StringStreamSource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Валидация XML-файла по Schematron-файлу
 */
@Slf4j
public class SchematronValidator {
    public static SchematronOutputType validateXMLViaPureSchematron(File schematronFile, String fixedXmlFile) throws Exception {
        final ISchematronResource aResSCH = SchematronResourceSCH.fromFile(schematronFile);
        if (!aResSCH.isValidSchematron()) {
            throw new IllegalArgumentException("Schematron file is corrupted");
        }
        return aResSCH.applySchematronValidationToSVRL(new StringStreamSource(fixedXmlFile));
    }

    public ValidationResult validate(String xmlContent, ValidatorMode validatorMode, Path schematronPath) {
        if (ValidatorMode.XSD.equals(validatorMode)) {
            return ValidationResult.SKIPPED;
        }

        try {
            log.info("Schematron validation started");
            log.info("Schematron file: «{}»", schematronPath);

            SchematronOutputType result = validateXMLViaPureSchematron(schematronPath.toFile(), xmlContent);
            List<SVRLFailedAssert> assertList = SVRLHelper.getAllFailedAssertions(result);
            for (int i = 0; i < assertList.size(); i++) {
                SVRLFailedAssert failedAssert = assertList.get(i);
                log.error("→ Failed Schematron rule #{}", i + 1);
                log.error("  Element: {}", failedAssert.getLocation());
                log.error("  Rule: {}", failedAssert.getText());
            }

            ValidationResult validationResult = assertList.isEmpty() ? ValidationResult.VALID : ValidationResult.NOT_VALID;
            log.info("Schematron validation completed");
            return validationResult;
        } catch (Exception e) {
            log.error("Schematron validation exception", e);
            return ValidationResult.NOT_VALID;
        }
    }
}
