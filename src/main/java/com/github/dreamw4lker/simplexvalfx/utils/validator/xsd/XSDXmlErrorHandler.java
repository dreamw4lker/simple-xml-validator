package com.github.dreamw4lker.simplexvalfx.utils.validator.xsd;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик ошибок по XSD-схеме.
 * Все ошибки уровня warning, error, fatalError направляются в массив exceptions.
 * После появления первой ошибки дальнейшая валидация НЕ останавливается.
 */
public class XSDXmlErrorHandler implements ErrorHandler {

    private final List<SAXParseException> exceptions;

    public XSDXmlErrorHandler() {
        this.exceptions = new ArrayList<>();
    }

    public List<SAXParseException> getExceptions() {
        return exceptions;
    }

    @Override
    public void warning(SAXParseException exception) {
        exceptions.add(exception);
    }

    @Override
    public void error(SAXParseException exception) {
        exceptions.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) {
        exceptions.add(exception);
    }
}