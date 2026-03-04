package com.github.dreamw4lker.simplexvalfx.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Base64ToXmlService {
    /**
     * Перевод Base64-строки в XML-строку
     * @param input строка в base64
     * @param formatXmlOutput следует ли форматировать получившийся XML
     * @return строка в XML
     */
    public String convertBase64ToXml(String input, boolean formatXmlOutput) {
        byte[] decodedBytes = Base64.decodeBase64(input);

        String xmlString = new String(decodedBytes, StandardCharsets.UTF_8);
        if (formatXmlOutput) {
            xmlString = formatXML(xmlString);
        }
        return xmlString;
    }

    /**
     * Форматирование XML-строки
     * @param xmlString XML-строка
     */
    private String formatXML(String xmlString) {
        if (ObjectUtils.isEmpty(xmlString)) {
            return "";
        }
        try {
            SAXReader reader = new SAXReader();
            reader.setValidation(false); //Не валидируем принятый XML

            Document document = reader.read(new StringReader(xmlString));

            StringWriter outputWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(outputWriter, OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();

            return outputWriter.toString();
        } catch (DocumentException e) {
            log.error("Не удалось отформатировать XML: ошибка при разборе", e);
            return "Не удалось отформатировать XML: ошибка при разборе. Подробности см. в логе приложения. Исходный текст:\n" + xmlString;
        } catch (IOException e) {
            log.error("Не удалось отформатировать XML: ошибка ввода/вывода", e);
            return "Не удалось отформатировать XML: ошибка ввода/вывода. Подробности см. в логе приложения. Исходный текст:\n" + xmlString;
        }
    }
}
