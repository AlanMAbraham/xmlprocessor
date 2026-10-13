package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.EmployeesXml;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class XmlParserService {

    private static final String NAMESPACE =
            "http://www.example.com/employee";

    private final XmlMapper xmlMapper = XmlMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public EmployeesXml parse(byte[] content) throws XMLStreamException, IOException {
        validateDocument(content);

        XMLStreamReader reader = newReader(content);

        try {
            EmployeesXml document =
                    xmlMapper.readValue(reader, EmployeesXml.class);

            if (document == null
                    || document.employees() == null
                    || document.employees().isEmpty()) {

                throw new IllegalArgumentException(
                        "XML must contain at least one employee");
            }

            return document;
        } finally {
            reader.close();
        }
    }

    private void validateDocument(byte[] content)
            throws XMLStreamException {

        XMLStreamReader reader = newReader(content);

        try {
            boolean rootFound = false;

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.DTD) {
                    throw new IllegalArgumentException(
                            "DOCTYPE declarations are not allowed");
                }

                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (!rootFound) {
                        if (!"employees".equals(reader.getLocalName())) {
                            throw new IllegalArgumentException(
                                    "Root element must be employees");
                        }

                        rootFound = true;
                    }

                    if (!NAMESPACE.equals(reader.getNamespaceURI())) {
                        throw new IllegalArgumentException(
                                "Unexpected XML namespace");
                    }
                }
            }

            if (!rootFound) {
                throw new IllegalArgumentException(
                        "XML document has no root element");
            }
        } finally {
            reader.close();
        }
    }

    private XMLStreamReader newReader(byte[] content)
            throws XMLStreamException {

        XMLInputFactory factory = XMLInputFactory.newFactory();

        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(
                XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        factory.setXMLResolver((publicId, systemId, baseUri, namespace) -> {
            throw new XMLStreamException(
                    "External XML resources are not allowed");
        });

        return factory.createXMLStreamReader(
                new ByteArrayInputStream(content));
    }
}