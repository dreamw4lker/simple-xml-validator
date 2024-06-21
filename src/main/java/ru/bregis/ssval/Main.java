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

import javax.annotation.Nonnull;
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
        if (args.length < 2) {
            log.error("Please, define two arguments:\n1. XML document\n2. Schematron file");
            return;
        }
        //В Schematron минздрава не указан namespace по умолчанию.
        //Вырезаем его из прочитанного документа
        String content = IOUtils.toString(new FileInputStream(args[0]), StandardCharsets.UTF_8);
        content = content.replaceFirst("<ClinicalDocument xmlns=\"urn:hl7-org:v3\"", "<ClinicalDocument xmlns=\"\"");

        log.info("Validating... Please, wait...");
        SchematronOutputType result = validateXMLViaPureSchematron(new File(args[1]), content);
        List<SVRLFailedAssert> assertList = SVRLHelper.getAllFailedAssertions(result);
        for (int i = 0; i < assertList.size(); i++) {
            SVRLFailedAssert failedAssert = assertList.get(i);
            log.error("Failed assert #{}: Element: {}, rule: {}", (i + 1), failedAssert.getLocation(), failedAssert.getText());
        }
    }
}
