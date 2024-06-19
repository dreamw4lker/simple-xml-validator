package ru.bregis.ssval;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.StringStreamSource;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static SchematronOutputType validateXMLViaPureSchematron(@Nonnull final File schematronFile, String fixedXmlFile) throws Exception {
        final ISchematronResource aResSCH = SchematronResourceSCH.fromFile(schematronFile);
        if (!aResSCH.isValidSchematron())
            throw new IllegalArgumentException("Invalid Schematron!");
        return aResSCH.applySchematronValidationToSVRL(new StringStreamSource(fixedXmlFile));
    }

    public static void main(String[] args) throws Exception {
        //В Schematron минздрава не указан namespace по умолчанию.
        //Вырезаем его из прочитанного документа
        String content = IOUtils.toString(new FileInputStream("/home/as/sval/doc.xml"), StandardCharsets.UTF_8);
        content = content.replaceFirst("<ClinicalDocument xmlns=\"urn:hl7-org:v3\"", "<ClinicalDocument xmlns=\"\"");

        SchematronOutputType result = validateXMLViaPureSchematron(new File("/home/as/sval/schematron_125.sch"), content);

        List<SVRLFailedAssert> assertList = SVRLHelper.getAllFailedAssertions(result);
        for (SVRLFailedAssert obj : assertList) {
            System.out.println(obj.getText());
        }
    }
}
